package com.leeo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.leeo.sys.user.entity.SysConfigProperties;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PropertiesTest{
   
    @Autowired
    private SysConfigProperties sysConfigProperties;

    @Test
    public void testProperties() throws Exception {

        System.out.println("PropertiesTest.testBlog()="+sysConfigProperties);
        System.out.println("name="+sysConfigProperties.getName());

        Assert.assertEquals("Angel",sysConfigProperties.getName());
        
        Assert.assertEquals("Spring Boot", sysConfigProperties.getTitle());

    }

}