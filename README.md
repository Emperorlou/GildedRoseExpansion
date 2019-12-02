# Overview
This application was built using Spring Boot and utilizes Spring's authentication framework 
for the access points that needed authentication. I made sure to use only the data structure
that the test document requested. One concern with the Item data structure is the lack of
an ID field and so I ended up using the name of the item as an ID in lieu of this. The 
application does not use a database and the users are done with an "in memory" system that
is hard-coded for 2 users. The solution I had for the surge pricing left me wanting to use
a memcache system that I'm used to from the GCP, which is essentially a super fast key-value 
store with expiry dates on the values. I decided to quickly write a mock class that would do
this as it allowed me to simplify the test and not require further dependencies. 

# Authentication
I briefly considered rolling my own authentication system since this is a simple test but I
opted to go with a solution that would more likely be represented in a real product. 
For this reason I opted to go with Spring's security framework. It is also the safest route 
to take as it is a system that is well tested and used by may other projects. Security is not 
something you usually want to take lightly so utilizing existing tried-and-true frameworks is
certainly the right way to go.

The `/user/**` sub-resource is locked down for all but authenticated users.

There is a `/logout` resource for logging out users as well. I opted to not put this API call
behind `/user` authentication because it would be annoying to have to handle programmatically.
If you were trying to logout but were not logged in, you'll get a 401 which would be entirely
unnecessary to have to handle from the client side and in this case getting a 401 would still
fire even though you're technically logged out.

## Web Login
There are 2 flavors of login which allows people to use the application manually. When using it
manually, simply going to a protected resource will prompt the user with a login/password popup in
the browser if the user is not logged in. Upon providing correct credentials, the original request
action will continue.

## API Login
The second login flavor is done through the `/login` resource. Specifying 
`/login?user=user1&pass=password` will also log the user in without a popup and will respond with JSON. 
It is expected that this method would be used programmatically. 

#Surge Pricing
I opted to use my mock memcache implementation for a key-value store (with value expiration) for this.
Since the test document requested that we create an endpoing that retrieves `the current inventory`, I took
this to mean that we were to only create a complete list of items endpoint. This meant that there would be no
individual item inspection and so implementing surge pricing on an individual item level would have been 
wasteful. My implementation, however, makes it very easy to migrate away from this concept of a 
"store wide" surge and instead employ the same surge pricing strategy on a per-item basis. Refactoring
for a change like this would be relatively trivial.

I considered storing individual views in a database, but this would likely lead to too much database chatter,
slowing it down. Instead I decided to go with a memcache view-cout storage solution. The downside of this
implementation is that memcached values are not persisted after a reboot (usually). The upside of this 
is that it is highly efficient and generally doesn't need to be overly accurate for what we're using it for.
If we started getting thousands of views per minute, the accuracy of the memcached view counters would start
to fail (at least for a real memcache; assuming a clustered server environment). We wouldn't bother 
using transactions or "safe" puts/gets, simply because it doesn't really matter here for the thresholds we'll
be interested in (currently the surge threshold is set to >10 in accordance with the test document).

## Implementation Details
All of the surge-related variables are customizable in-code, and you can see their output by going to
the `/admin/config` resource. The output from this resource also makes it easy to see what is happening
with the surge in realtime (by refreshing this page). 

The test document requested that we have a 60 minute period that is to be tracked for view surges with a 
surge being defined as `more than 10 times in an hour`. So in a given hour if the number of views reaches
11 or more, we're to be considered to be in a surge state. What I did was break down the 60 minute period
into 2 "ranges" of 30 minutes each. Each range is stored as a separate value in memcache with the latest 
views increasing the current 30 minute range view counter. As time continues to pass and we enter the next
30 minute range, the view counter starts at 0 for that range but we include the previous 30 minute counter
to determine if we're in a surge state. 

There is more to it though...

I added a 3rd range as well and I did this for 2 reasons.

1) It ensures that we will be in a surge state for at LEAST 1 hour and at most 1.5 hours if at any point
we recieved at least 11 views in any hour.
2) I'm considering that customers likely wouldn't appreciate seeing a fluctuating price. Even
if one were to record every single view and forego "bucketing" the views the way I have here, in a scenario 
where we are getting between 10 and 11 views per hour, the surge pricing state could fluctuate minute to minute.
With the surge strategy I have used, the surge could last for up to 30 minutes after the initial hour, but it also
means that the surge state will not fluctuate as often even in the worst case scenario.

To explain in a more practical way. Lets say it is 4pm now. This means that our 3 ranges are for 3:30-4pm, 
3:00-3:30, and 2:30-3:00. View counters are stored for each of these 3 ranges separately. Lets call each of these
3 ranges A, B, and C (A being the 3:30-4pm range). Using this, we consider the store to be in a surge state if
we the sum of the number of views for A and B is greater-than the surge trigger (in this case 10) **OR** if the sum of B and C
is greater-than the surge trigger (10).

Overall I believe this strategy to be highly efficient and ready to work in a large scale.
 
#Data Format
I went with a JSON response format for all off the API endpoints as I believe this to be an appropriate, modern
response style for REST services. 

##Item List
Fetching all items in the store:
`http://localhost:8080/items`

This call will result in a JSON response that looks like this: 

```$xslt
[
{
"name": "Door handle",
"description": "As always, the most excellent door handle money can buy.",
"price": 3500
},
{
"name": "Lamp",
"description": "An excellent, green, decorative lamp.",
"price": 6000
},
{
"name": "Frog leg",
"description": "Delicious in soup, also a great substitute for chicken wings!",
"price": 200
}
]
```

##Login
Login method generally used by applications:

`http://localhost:8080/login?user=user1&pass=password`

This will result in a success (or failure) JSON message like this:

`{"response":"success"}`

##Logout
Logging out of the system.

`http://localhost:8080/logout`

This will result in a success JSON message like this: 

`{"response":"success"}`

##Buy Item
Buying an item. Since items have no quantity (the provide data structure lacked a quantity) and since there
is no data structure for users, there is nothing (besides authentication) that is stopping people from buying
an item. If you attempt to call this endpoint without being logged in, you will be given a 401 response.
If you are on a browser and you perform this call manually, your browser will give you a popup to enter a 
username and password. 

Please note this was only tested on Chrome.

`http://localhost:8080/user/buyitem?itemName=Lamp`

This will result in a success JSON message like this:

```$xslt
{
"costCents": 6000,
"user": "user1",
"status": "succeeded"
}
```