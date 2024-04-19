db.createUser({
    user: "rootuser",
    pwd: "only_i_know_what_i@_have_been_thinking",
    roles: [
        {
            role: "readWrite",
            db: "eighteen"
        }
    ]
});