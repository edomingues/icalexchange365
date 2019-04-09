package com.edomingues.icalexchange365;

import com.edomingues.icalexchange365.service.AuthenticationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Icalexchange365ApplicationTests {

	@MockBean
	private AuthenticationService authenticationService;

	@Test
	public void contextLoads() {
	}

}
