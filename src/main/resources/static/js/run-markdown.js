let markdowns = document.getElementsByClassName('markdown');
for (i = 0; i < markdowns.length; i++) {
    let temp = marked.parse(markdowns[i].innerHTML);
    // There may some bugs in <pre><code>
    // So replace &amp; to &
    let reg = /<pre><code.*?>[\s\S]*<\/code><\/pre>/g;
    let match = temp.match(reg);
    if (match) {
        for (j = 0; j < match.length; j++) {
            let code = match[j];
            code = code.replace(/&amp;/g, '&');
            temp = temp.replace(match[j], code);
        }
    }
    markdowns[i].innerHTML = temp;
}