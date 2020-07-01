function initNavListeners(){
	console.log('Initializing navigation listeners');
	
	$('.navPills').on('click', function(event) {
		event.preventDefault();
		var buttonId = $(this).attr('id');
		var href = $(this).attr('href');
		
		switch(buttonId) {
			case 'fragNav':
				requestURL(href);
				break;
			case 'editNav':
				requestEditor(href);
				break;
			default:
				console.warn('Call to unknown .nav : ignored');
		}

	});
	
	$('.navPills:not(.active)').on({
		mouseenter : function() {
			$(this).css("background-color", "#a5a5a5");
		},
		mouseleave : function() {
			$(this).css("background-color", "#ffffff");
		}
	});
	
}

function initEditorListeners(){
	console.log('Initializing editor listeners');

	$('.ed-init').off().on('click', function(event) {
		event.preventDefault();
		$('.ed-init').removeClass('active');
		$(this).addClass('active');
		
		var href = $(this).attr('href');
		requestEditor(href);
	});
	
	$('.ok').on('click', function(event){
		event.preventDefault();
		var buttonId = $(this).attr('id');
		
		switch(buttonId) {
			case 'dateOk':
				var value = $('#datetime-input').val();
				
				replaceFormValue('#form-date', value);
				replaceText('#table-date', value.replace('T', ' '));
				break;
				
			case 'courseOk':
				var id = $(this).find('#selectId').text();
				var text = $(this).find('#selectSubj').text();
				
				replaceFormValue('#form-courseId', id);
				replaceText('#table-course', text);
				markEditorRows(getFormValuesById('#form-courseId'));
				break;
				
			case 'teacherOk':
				var id = $(this).find('#selectId').text();
				var firstName = $(this).find('#selectFirstName').text();
				var lastName = $(this).find('#selectLastName').text();
				
				replaceFormValue('#form-teacherId', id);
				replaceText('#table-teacher', firstName + ' ' + lastName);
				markEditorRows(getFormValuesById('#form-teacherId'));
				break;
			
			case 'studentOk':
				var id = $(this).find('#selectId').text();
				
				if($(this).hasClass(markClass)){
					$("#form-studentId[value='" + id + "']").remove();
				} else {
					$("#form-students").append(
							'<input id="form-studentId" type="hidden" ' + 
							'name="studentId" value="' + id + '">');
				}
				
				var formIds = getFormValuesById('#form-studentId');
				markEditorRows(formIds);
				replaceText('#table-students', formIds.length);
				break;
				
			default:
				console.warn('Call to unknown .ok : ignored');
		}
	});
}
