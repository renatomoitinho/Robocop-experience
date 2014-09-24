var app, obj, render, user;

render = function(result) {
    this.result = result;
    this.template = function(template, data) {
        var e;
        try {
            return this.result.writeHTML(Mustache.render(this.result.template(template), data));
        } catch (_error) {
            e = _error;
            return this.result.writeError("template error " + e.message + " in " + template);
        }
    };
    this.text = function(text, data) {
        var e;
        try {
            return this.result.writeText(Mustache.render(text, data));
        } catch (_error) {
            e = _error;
            return this.result.writeError("template error " + e.message + " in " + text);
        }
    };
    return this;
};

app = {};

app.index = function(result) {
    var msg;
    msg = {
        text: "hello text"
    };
    return result.render.template('welcome', {
        name: "Robotscop framework :) ",
        text: msg
    });
};

app.status = function(result) {
    return result.status(parseInt(result.param.status));
};

app.redirect = function(result) {
    if (result.param.template !== null) {
        return result.render.template(result.param.template.replace(".", "/", {}));
    } else {
        return result.status(404);
    }
};

user = {};

user.index = function(result) {
    var users;
    users = JSON.parse(result.all());
    return result.render.template("user/users", {
        data: users
    });
};

user.get = function(result) {
    return result.render.template("user/show", JSON.parse(result.get(result.param.id)));
};

user.update = function(result, req) {
    if (req.getMethod() === "POST") {
        result.update(result.param.id, JSON.stringify(result.param));
        result.redirect("/user");
        return undefined;
    }
    return result.render.template("user/form-update", JSON.parse(result.get(result.param.id)));
};

user.remove = function(result) {
    result["delete"](result.param.id);
    return result.redirect("/user");
};

user.save = function(result) {
    var errors;
    errors = [];
    if (result.param.name === "") {
        errors.push("name not found");
    }
    if (result.param.email === "") {
        errors.push("email not found");
    }
    if (_.size(errors) !== 0) {
        return result.render.template("user/form", _.extend({
            errors: errors
        }, result.param));
    } else {
        result.save(JSON.stringify(result.param));
        return result.redirect("/user");
    }
};

obj = Object;

obj.on = function(query, result, req, resp) {
    var e, val;
    try {
        val = eval(query);
        result.render = new render(result);
        result.param = JSON.parse(result.getParam());
        return val(result, req, resp);
    } catch (_error) {
        e = _error;
        return result.status(500, "" + e.message);
    }
};
