var elements = document.getElementsByClassName('markdown');
for (i = 0; i < elements.length; i++) {
    elements[i].innerHTML = marked.parse(elements[i].innerHTML);
}