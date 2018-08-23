var stompClient = null;
var user = null;
var userList = null;
var chatWithUser = null;
var chatStack = new Array();
var load = function(){
	console.log("windows loaded");
	var cookie = getCookie();
	if(cookie!=""){
		console.log(cookie);
		var xmlHttp = new XMLHttpRequest();
		xmlHttp.onreadystatechange = function(){
			console.log("something:"+xmlHttp.readystate+" "+xmlHttp.status)
			 if (xmlHttp.readyState == 4 && xmlHttp.status == 200){
				var json = JSON.parse(this.responseText);
				user = json.username;
				document.getElementById("username").innerHTML = user;
				//Automatic connect to Websocket
				connect();
			    
			}
		}
		xmlHttp.open("GET","http://localhost:8080/users/current");
		xmlHttp.setRequestHeader("Authorization","Bearer "+cookie);
		xmlHttp.send(null);
	}else{
		console.log(cookie);
		window.location.href = "http://localhost:8080/views/login.html"
	}
};
	
function getCookie(){
	 var name = "token=";
	    var decodedCookie = decodeURIComponent(document.cookie);
	    var ca = decodedCookie.split(';');
	    for(var i = 0; i < ca.length; i++) {
	        var c = ca[i];
	        while (c.charAt(0) == ' ') {
	            c = c.substring(1);
	        }
	        if (c.indexOf(name) == 0) {
	            return c.substring(name.length, c.length);
	        }
	    }
	    return "";
	}
function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
	var token = getCookie();
    var socket = new SockJS('/gs-guide-websocket?token='+token);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
       //This is user specific queue. This will be used in the chat where a specific user want
        //to send message to specific user. This will receive the message display.
        stompClient.subscribe('/user/queue/message', function (message) {
        	var chatMessage = JSON.parse(message.body);
        	displayChatBox(chatMessage.sender);
        	showMessage(JSON.parse(message.body));
        });
        
        // This is a notification queue which receives the user who logged in and display 
        stompClient.subscribe('/queue/online', function (message) {
        	console.log(JSON.stringify(message.body))
        	addUserToOnlineUser(JSON.parse(message.body));
        });
        
    });
    request();
   
}
function request(){
	var token = getCookie();
	 var xmlHttp = new XMLHttpRequest();
	 xmlHttp.onreadystatechange = function(){
		 if(xmlHttp.readyState == 4 && xmlHttp.status == 200){
			 userList = JSON.parse(this.responseText);
			 userList.forEach(function(entry){
				var message = {'user':entry.username,'action':'CONNECTED'};
				addUserToOnlineUser(message);
			 });
		 }
	 } 
	 	xmlHttp.open("GET","http://localhost:8080/users/connected");
		xmlHttp.setRequestHeader("Authorization","Bearer "+token);
		xmlHttp.send(null);
}
function addUserToOnlineUser(message){
	var onlineUser = message.user;
	var action = message.action
	if(onlineUser!=user&&onlineUser&&!ifAlreadyAdded(onlineUser)&&action=='CONNECTED'){
		$("#online_users").append("<li>"
			+"<div class='user_profile' id="+onlineUser+">"
				+"<img src='download.png' align='middle' class='profile_picture'/><br>"
				+"<p><b>"+onlineUser+"</b></p>"
				+"<p>My Bio</p>"
			+"</div>"+
			"</li>");
	}else if(action=='DISCONNECTED'){
		$("#"+onlineUser).remove();
	}
}
function ifAlreadyAdded(user){
	var parent = document.getElementById("online_users");
	var child = parent.firstChild
	while(child) {
		console.log(child.nodeValue);
		if(user==child.innerHTML&&child.nodeType == 1){
			return true;
		}
	    child = child.nextSibling;
	}
}
function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}
function send(){
	
	stompClient.send("/app/message", {}, JSON.stringify({'message': $("#message").val()
    	,'receiver':chatWithUser,'sender':user}));
    $("#chatbox").append("<p align='right'>"+$('#message').val()+"</p>");
}

function showMessage(message) {
	$("#chatbox").append("<p align='left'>"+ message.message +"</p>");
}
function displayChatBox(id){
	userList.forEach(function(entry){
	 if(entry.username == id&&!($.inArray(id, chatStack ) > -1)){
		 chatWithUser = id;
		 chatStack.push(id);
	$("#chatarea").append(
	"<div class='chat_topbar'>"+
	"<div style='margin:0px;padding:5px;'>"+
		"<div style='float:left;display:inline-block;margin-left:5px;'><img src='download.png' align='middle' class='profile_picture'/></div>"+
		"<div style='margin-top:5px;margin-left:5px;display:inline-block'><h4 style='margin-top:2px;margin-bottom:0px;'>"+entry.firstName+" "+entry.lastName+"</h4><p>"+entry.username+"</p></div>"+
	"</div>"+
		"</div>"+
		"<hr>"+
		"<div class='chatbox' id='chatbox'>"+
			
		"</div>"+
		"<div class='chattext'>"+
			"<input type='text' id='message' name='chattext' style='height:50%; width:100%'>"+
			"<button type='button' class='btn btn-primary' onclick='send()'>Send</button>"+
		"</div>"
		);
	 }
	});
}
$(function () {
	
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#online_users").on("click","li",function(event){
        var id = event.target.id;
        displayChatBox(id);
    });
});