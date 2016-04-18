    curl start.spring.io/starter.tgz \
           -d groupId=com.metflix \
           -d artifactId=recommendations \
           -d packageName=com.metflix \
           -d baseDir=recommendations \
           -d dependencies=web,actuator \
           -d applicationName=RecommendationsApplication | tar -xzvf -


    curl start.spring.io/starter.tgz \
           -d groupId=com.metflix \
           -d artifactId=membership \
           -d packageName=com.metflix \
           -d baseDir=membership \
           -d dependencies=web,actuator \
           -d applicationName=MembershipApplication | tar -xzvf -



    curl -XPOST http://localhost:4444/api/members -H 'Content-Type: application/json' -d '{"user":"Taro", "age" : 20}'
    curl http://localhost:4444/api/members/making