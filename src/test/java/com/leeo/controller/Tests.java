package com.leeo.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class Tests {

	public static void main(String[] args) {
		String ids = "1,,2,a";
		List<Long> a = Arrays.asList(StringUtils.split(ids, ',')).stream()
//				.filter(id -> StringUtils.isNotBlank(id))
				.filter(id -> NumberUtils.isNumber(id))
				.map(id -> Long.valueOf(id))
				.collect(Collectors.toList());
		
		System.out.println(a);
	}

}
