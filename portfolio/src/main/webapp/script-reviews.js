function printableMessage(comments){
    console.log(comments);
    var s = "";
    for(var i = 0; i < comments.length; i++){
        s += ("<div class='card grid-item i" + (i + 1) + "'> <img src='images/img_avatar_m.png' alt='Avatar' style='width:100%'> <div class='container'> <h4><b>" 
        + comments[i]["name"] + "</b></h4> <p>" 
        + comments[i]["message"] 
        + "</p> </div> </div>");
        //s += comments[i]["name"] + " says " + comments[i]["message"] + "(" + comments[i]["score"] + ")\n";
    } 
    
    return s;    
}

function getRandomQuoteUsingArrowFunctions() {
  fetch('/data')
  .then(response => response.json())
  .then((msg) => {
    document.getElementById('hello').innerHTML = printableMessage(msg);
  });
}
