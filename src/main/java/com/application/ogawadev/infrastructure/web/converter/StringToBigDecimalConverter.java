package com.application.ogawadev.infrastructure.web.converter;

import java.math.BigDecimal;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.application.ogawadev.application.util.FormatUtils;
import com.application.ogawadev.application.util.StringUtils;

@Component
public class StringToBigDecimalConverter implements Converter<String, BigDecimal>{

	@Override
	public BigDecimal convert(String source) {
		
		if(StringUtils.isEmpty(source)) {
			return null;
		}
		
		source = source.replace(",",".").trim();
		
		
		
		return BigDecimal.valueOf(Double.valueOf(source));
	}

}
