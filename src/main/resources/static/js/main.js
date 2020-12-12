var markClass = 'alert-success';

$.ajaxSetup({ cache: true, async: true });

$(document).ajaxError(function(e, xhr, opt, thrownError) {
	console.log('ajax call failed');
	setSectionTitle('Error');
	
	var errorPage = '<div class="container text-center">' +
	'<p class="text-warning h1">' +	xhr.status + ' ' + xhr.statusText + '</p><br />' + 
	'<p class="text-danger h3">Request failed: ' + opt.url + ' ' + thrownError + '<br /><br />' +
	'<img class="img-fluid" alt="thinking" src="img/thinking.jpg"></img></p></div>';

	$('.fragmentTarget').html(errorPage);
});

function requestSection(url, title){
	setSectionTitle(title);
	requestURL(url);
}

function setSectionTitle(title){
	$("#sectionTitle").text(title);
}

function requestURL(url, type='GET', _callback){
	console.log("url requested: " + url + ", using method: " + type);
	$.ajax({
		   url: url,
		   type: type,
		   dataType: "html",
		   success: function(html) {
			   
			   var modalIndex = $('<div />')
			   		.append(html)
			   		.find('.modalFragment')
			   		.index();
			   
			   if(modalIndex === -1){
				   $(".fragmentTarget").html(html);
			   } else {
				   $(".modalTarget").html(html);
			   }
			   
				if(_callback !== undefined){
					_callback();
				}
		   	} 
		});
}

function submitForm(responseTarget='.fragmentTarget'){
	var formURL = $(".form").attr("action");
	$.ajax({
		url : formURL,
		type: 'POST',
		data: $('.form').serialize(),
		dataType : 'html',
		success : function (response) {
			
			 var modalIndex = $('<div />')
		   		.append(response)
		   		.find('.modalFragment')
		   		.index();
		   
		   if(modalIndex === -1){
			   $(responseTarget).html(response);
		   } else {
			   $(".modalTarget").html(response);
		   }
	
		}
	});
}

function launchEditor(element, url, type = 'GET'){
	requestURL(url, type, function(){
		var text = $(element).text();
		if(text === 'Create'){
			$('.form-group #id').hide();
		}
		$('.modal-title').text($(element).text());
		$('.modalFragment').modal();
	});
}

function requestEditor(url, type='GET', _callback){
	$.ajax({
		   url: url,
		   type: type,
		   dataType: "html",
		   success: function(html) {  
			   $(".editorTarget").html(html);
			   if(_callback !== undefined){
				   _callback();
			   }
		  }
	});
}

function requestDelete(url, msg='Do you with to continue?'){
	var deleteLink = 'requestURL(\'' + url +  '\', \'POST\')';
	
	$('#confirmationModal #confirmedReference')
		.attr('onclick', deleteLink).text('Delete');
	$('#confirmationModal #confirmationLabel')
		.text('DELETE');
	$('#confirmationModal #confirmationMessage')
		.text(msg);
	$('#confirmationModal').modal();
}

function getFormValuesById(id){
	var addedIds = $('.form ' + id);
	var formIds = [];
	
	addedIds.each(function(){
		formIds.push($(this).attr('value'));
	});
	
	return formIds;
}

function markEditorRows(formIds){
	$('.editorTarget .ok').each(function(){
		var id = $(this).find('#selectId').text();
		
		if(formIds.indexOf(id) !== -1){
			$(this).addClass(markClass);
		} else if($(this).hasClass(markClass)){
			$(this).removeClass(markClass);
		}
	});
}

function replaceFormValue(inputId, value){
	$(inputId).attr('value', value);
}

function replaceText(selector, text){
	$(selector).text(text);
}

function toPage(url){
	location.replace(url);
}

function fireClickEvent(element){
	$(document).ready(function(){
		element.click();
	});
}

function removeNavElements(){
	$("#sectionTitle, .navItems").remove();
}