package com.application.ogawadev.infrastructure.web.validator;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import com.application.ogawadev.application.util.FileType;

public class UploadValidator implements ConstraintValidator<UploadConstraint, MultipartFile>{

	private List<FileType> acceptedFileTypes;
	
	@Override
	public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
		
		if(multipartFile == null) {
			return true;
		}
		
		for (FileType fileType : acceptedFileTypes) {
		
			if(fileType.sameOf(multipartFile.getContentType())) {
				
				return true;
			}
			
		}
		
		return false;
	}

	@Override
	public void initialize(UploadConstraint constraintAnnotation) {
		acceptedFileTypes = Arrays.asList(constraintAnnotation.acceptedTypes());
		
	}
	 
	
}
