package mobi.allshoppings.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.tools.SystemStatusService;

@Aspect
public class SystemCodeAuditAdvisor {

	@Autowired
	SystemStatusService service;
	
//	private static final Logger log = Logger.getLogger(SystemCodeAuditAdvisor.class.getName());
	
//	@After("execution(* mobi.allshoppings.apdevice..*.*(..))")
//	@Pointcut("within(mobi.allshoppings..*)")
//	@Pointcut("execution(* mobi.allshoppings..*.*(..))")
//	@After("execution(* *(..))")
	@After("execution(* mobi.allshoppings..*.*(..))")
	public void afterMobiAllshoppingsAPDevice(JoinPoint joPoint) {
//		log.log(Level.INFO, joPoint.toShortString());
		System.out.println(joPoint);
	}

}
