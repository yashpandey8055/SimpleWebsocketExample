
var register = function(){
			var xmlHttp = new XMLHttpRequest();
    	    xmlHttp.onreadystatechange = function() { 
    	        if (xmlHttp.readyState == 4 && xmlHttp.status == 200){
    	        	document.getElementById("test").innerHTML =
    	        	      this.responseText;
    	       
    	        document.cookie="token="+ this.responseText;
    	        window.location.href = env+"/views/index.html";
    	        }else if(xmlHttp.readyState == 4 &&xmlHttp.status == 404){
    	        	document.getElementById("test").innerHTML =
  	        	      this.responseText;
    	        }
    	    }
    	    var username = document.getElementById("username").value;
    	    var password = document.getElementById("password").value;
    	    xmlHttp.open("GET", env+"/public/users/login?userName="+username, true); // true for asynchronous 
    	    xmlHttp.send(null);
    	    
    	}