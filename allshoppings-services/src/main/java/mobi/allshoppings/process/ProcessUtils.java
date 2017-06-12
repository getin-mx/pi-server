package mobi.allshoppings.process;
import mobi.allshoppings.exception.ASException;

public interface ProcessUtils {
	
	void startProcess(String identifier, boolean wait) throws ASException;
	void cancelPendingProcesses() throws ASException;
	void completePendingProcesses() throws ASException;
}
