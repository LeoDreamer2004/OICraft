const text = document.querySelector('.blinking-code');
const txt = [
    'print("Hello OICraft!")', // Python
    'System.out.println("Hello OICraft!");', // Java
    'echo "Hello OICraft!";', // PHP
    'printf("Hello OICraft!");', // C
    'cout << "Hello OICraft!" << endl;', // C++
    'console.log("Hello OICraft!");', // JavaScript
    'println!("Hello OICraft!");', // Rust
    'fmt.Println("Hello OICraft!")', // Go
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