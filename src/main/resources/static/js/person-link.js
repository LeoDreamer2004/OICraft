// 对所有class为comment的元素，如果出现 @xxx 的字符串，就把它替换成链接
// 现在发请求'/api/user?username=' + username，返回的text作为用户的 ID，链接为/profile/{ID}
const elements = document.getElementsByClassName('user-comment');
for (let i = 0; i < elements.length; i++) {
    const html = elements[i].innerHTML;
    const re = /@([a-zA-Z0-9_]+)/g;
    elements[i].innerHTML = html.replace(re, function (match, username) {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', '/api/user?username=' + username, false);
        xhr.send();
        if (xhr.status === 200) {
            const userId = xhr.responseText;
            return '<a href="/profile/' + userId + '">' + match + '</a>';
        } else {
            return match;
        }
    });
}