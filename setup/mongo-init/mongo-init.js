db.createUser({
    user: 'eighteen-admin',
    pwd: 'eighteen-admin-password',
    roles: [
        {
            role: 'readWrite',
            db: 'eighteen',
        },
    ],
});

db = new Mongo().getDB("eighteen");

db.createCollection('test', {capped: false});

db.test.insert([
    {"item": 1},
    {"item": 2},
    {"item": 3},
    {"item": 4},
    {"item": 5}
]);