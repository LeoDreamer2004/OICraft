const text = document.querySelector('.blinking-code');
const txt = [
    'print("Hello world!")', // Python
    'System.out.println("Hello world!");', // Java
    'echo "Hello world!";', // PHP
    'printf("Hello world!");', // C
    'cout << "Hello world!" << endl;', // C++
    'console.log("Hello world!");', // JavaScript
    'println!("Hello world!");', // Rust
    'fmt.Println("Hello world!")', // Go

];
let index = 0;
let i = 0;
let output = true;

setInterval(function () {

    if (output) {
        text.innerHTML = txt[i].slice(0, ++index);
        console.log(index);
    } else {
        text.innerHTML = txt[i].slice(0, index--);
        console.log(index);
    }

    if (index === txt[i].length + 15) {
        output = false;
    } else if (index < 0) {
        index = 0;
        output = true;
        i++;
        if (i >= txt.length) {
            i = 0;
        }
    }

}, 60)