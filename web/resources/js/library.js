function checkValue(form, message){
    
    var userInput = form[form.id + ":username"];
    
    if (userInput.value ==''){
        alert(message)
        userInput.focus();
        return false;
    }
    
    return true;
}