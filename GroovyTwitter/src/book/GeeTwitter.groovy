package book

import twitter4j.*

class GeeTwitter {
	static twitter = null
	
	static void search(terms,Closure c) {
		if (!twitter)
			twitter = new Twitter()
		def query = new Query(terms)
		twitter.search(query).tweets.each {
			c.call(it.fromUser,it.text)
		}
	}

}


//GeeTwitter.search("Groovy DSL") { from, tweet ->
//	println "${from} : ${tweet}"
//}