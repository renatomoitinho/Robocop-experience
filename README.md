# (Robocop) Create your Controller/View in MVC
## with JavaScript v8 engine and your Model in Java[framework test experience]

mapper your routes in /conf/routes

```
#   Home page
    GET     /                            app.index
```

implements your controller in javascript/coffeScript

```javascript
app.index = function(result) {
    return result.render.template('welcome', {
        name: "Robotscop framework :) ",
        text: { text: "hello" }
    });
};
```

or coffee

```script
app.index = (result) ->
  result.render.template 'welcome',
    name: 'Robotscop framework :) '
    text: text: 'hello'
```

view with angular and mustache template

```html
<div>
    <label> Name:
        <input type="text" value="{% text.text %}" ng-model="yourName" placeholder="{% text.text %}"/>
    </label>
    <hr>
    <h1>it's angular {{yourName}}!</h1>
</div>
```

### Basic useful :+1:

<b>1)</b> git clone https://github.com/renatomoitinho/robotscop-test.git

<b>2)</b> download jar file for your platform
https://code.google.com/p/jav8/

mac: jav8-jsr223-Mac OS X-x86_64-0.6.jar <br/>
linux: jav8-jsr223-linux-amd64-0.6.jar    <br/>
win: jav8-jsr223-win-amd64-0.6.jar        <br/>

<b>3)</b> force dependency in maven

mvn install:install-file <b>-Dfile=path you jar</b> -DgroupId=engine.transaction -DartifactId=jv8 -Dpackaging=jar -Dversion=1.0 -DgeneratePom=true

run maven

<b>4)</b> mvn clean install

<b>5)</b> go to folder target and extract robocop-package.zip and execute jar file

java -jar robocop-jar-with-dependencies.jar

check in browser http://localhost:9000

:)


