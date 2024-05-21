let markdowns = document.getElementsByClassName('markdown');
for (i = 0; i < markdowns.length; i++) {
    markdowns[i].innerHTML = marked.parse(markdowns[i].innerHTML);
}