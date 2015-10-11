package TimeTracker.Settings;

import java.io.Serializable;
import java.util.Date;

public class ConfigSyncAccount_GoogleTasks implements Serializable {
	private static final long serialVersionUID = 2L;
	public Boolean	enabled;
	public Date		lastUpdate;
	
	public ConfigSyncAccount_GoogleTasks() {
		enabled = true;
		lastUpdate = null;
	}
}