render = (result) ->
  @result = result
  @template = (template,data) ->
    try
      @result.writeHTML Mustache.render @result.template(template),data
    catch e
      @result.writeError "template error #{e.message} in #{template}"

  @text = (text, data)->
    try
      @result.writeText Mustache.render text,data
    catch e
      @result.writeError "template error #{e.message} in #{text}"
  this

#
# controller application
#
app={}
app.index=(result)->
  msg= text:"hello text"
  result.render.template('welcome',{ name: "Robotscop framework :) " , text: msg })

app.status=(result)-> result.status parseInt(result.param.status)

app.redirect=(result)->
  if result.param.template isnt null
    result.render.template(result.param.template.replace ".", "/", {})
  else
    result.status 404
#
# users
#
user={}
user.index=(result)->
  users = JSON.parse result.all()
  result.render.template("user/users",{data: users })

user.get=(result)-> result.render.template("user/show", JSON.parse result.get result.param.id )

user.update=(result, req)->
  if req.getMethod() is "POST"
    result.update(result.param.id, JSON.stringify(result.param))
    result.redirect("/user")
    return

  result.render.template("user/form-update", JSON.parse result.get result.param.id)

user.remove=(result)->
  result.delete result.param.id
  result.redirect("/user")

user.save=(result)->
  errors = []
  errors.push "name not found" if result.param.name is ""
  errors.push "email not found" if result.param.email is ""

  if _.size(errors) isnt 0
    result.render.template("user/form", _.extend {errors: errors},result.param )
  else
    result.save( JSON.stringify(result.param) )
    result.redirect("/user")



# interface invoiced
obj = Object
obj.on = (query,result, req, resp) ->
  try
    val = eval query
    result.render =new render(result)
    result.param  = JSON.parse result.getParam()

    val result,req,resp
  catch e
    result.status 500,"#{e.message}"



#### main ####
#obj = Object
#obj.on = (query,result, req, resp) ->
#  val = eval query
#  result.render = render(result)
#  result.param = JSON.parse result.getParam()

#  val result,req,resp