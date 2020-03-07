function printableMessage(comments){
    console.log(comments);
    var s = "";
    for(var i = 0; i < comments.length; i++){
        s += comments[i]["name"] + " says " + comments[i]["message"] + "(" + comments[i]["score"] + ")\n";
    } 
    
    return s;
}

function getRandomQuoteUsingArrowFunctions() {
  fetch('/data')
  .then(response => response.json())
  .then((msg) => {
    document.getElementById('hello').innerText = printableMessage(msg);
  });
}
