let comments = document.getElementsByClassName('user-comment');
let userMap = {};
for (let i = 0; i < comments.length; i++) {
    let html = comments[i].innerHTML;
    let re = /@([a-zA-Z0-9_]+)/g;
    comments[i].innerHTML = html.replace(re, function (match, username) {
        if (userMap[username]) {
            return '<a href="/profile/' + userMap[username] + '">' + match + '</a>';
        }
        let xhr = new XMLHttpRequest();
        xhr.open('GET', '/api/user?username=' + username, false);
        xhr.send();
        if (xhr.status === 200) {
            let userId = xhr.responseText;
            userMap[username] = userId;
            return '<a href="/profile/' + userId + '">' + match + '</a>';
        } else {
            return match;
        }
    });
}