package book

import twitter4j.*

def twitterId = "Jacob_OpenRules"
def password = "jacob007"
	
def twitter = new Twitter(twitterId, password)

//twitter.updateStatus("Updating my status via the Twitter4J API")

//println twitter.getFriendsIDs() //UserDetail(twitterId).getStatusText()
twitter.directMessages.each { DirectMessage msg ->
	println "Message: ${msg.text} \nFrom: ${msg.sender.name} \nTime: ${msg.createdAt} "
}

//def query = new Query("Groovy DSL")
def query = new Query("from:glaforge DSLs")
twitter.search(query).tweets.each { tweet ->
	println "FROM: ${tweet.fromUser} : ${tweet.text}"
}

GeeTwitter.search("Groovy DSL") { from, tweet ->
println "${from} : ${tweet}"
}