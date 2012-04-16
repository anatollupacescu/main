package core.wf;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.model.Message;
import core.node.GenericNode;

public class Workflow {

	private final static String DELIM = ",";
	private final static String WORKFLOW_STATES = "workflow.states"; 
	private final static String WORKFLOW_TRANSITION = "workflow.transition.";
	private final static String WORKFLOW_TRANSITION_KEYS = "workflow.transition.keys.";
	private final static String WORKFLOW_STATE_ACTION="workflow.transition.action.";
	private final static String WORKFLOW_STATE_ACTION_ARGS=WORKFLOW_STATE_ACTION + "args.";
	private static final String WORKFLOW_MODEL = "workflow.model";
	
	private final static Logger logger = Logger.getLogger(Workflow.class.getName());
	
	private final StateMachine<String, String> workflow = new StateMachine<String, String>();
	private final Map<String, GenericNode> actions = new HashMap<String, GenericNode>();
	
	private final String className;
	public final String initialState;
	
	public Workflow(String fileName) throws Exception {

		Properties properties = new Properties();

		properties.load(new FileInputStream(fileName));

		className = properties.getProperty(WORKFLOW_MODEL);
		try {
			Class.forName(className).getConstructor(String.class, String.class);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Could not create class for : " + className, e);
			throw new RuntimeException(e);
		}
		
		/* populate states */
		String states = properties.getProperty(WORKFLOW_STATES);
		List<String> stateList = new LinkedList<String>(Arrays.asList(states.split(DELIM)));

		if (stateList.size() < 2) {
			throw new Exception("At least two states should be specified in " + WORKFLOW_STATES);
		}

		initialState = stateList.get(0);
		
		/* getting the transitions, except for the last, ending state */
		for (int i = 0; i < stateList.size() - 1; i++) {

			GenericNode action = null;
			
			String startState = stateList.get(i);
			String stateNode = properties.getProperty(WORKFLOW_STATE_ACTION + startState);

			String actionArgs = properties.getProperty(WORKFLOW_STATE_ACTION_ARGS + startState);
			
			if (actionArgs != null) {
				
				String[] args = null;
				
				if(actionArgs.contains(DELIM)) {
					args = actionArgs.split(DELIM);
				}else{
					args = new String[] { actionArgs };
				}
				
				final Class<?>[] classArgTypes = { String[].class };
				final Object[] objectArgs = { args };
				
				try {
					Class<?> clazz = Class.forName(stateNode);
					Constructor<?> c = clazz.getConstructor(classArgTypes);
					action = ((GenericNode) c.newInstance(objectArgs));
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Could not create class for : " + stateNode + " with args : " + actionArgs, e);
					throw new RuntimeException(e);
				}
			} else {
				try {
					Constructor<?> c = Class.forName(stateNode).getConstructor();
					action = ((GenericNode) c.newInstance());
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Could not create class for : " + stateNode, e);
					throw new RuntimeException(e);
				}
			}

			String keys = properties.getProperty(WORKFLOW_TRANSITION_KEYS + startState);
			String[] keyArray = keys.split(DELIM);

			for (String key : keyArray) {

				String endState = properties.getProperty(WORKFLOW_TRANSITION + startState + "." + key);

				if (!stateList.contains(endState)) {

					throw new RuntimeException("Destination state " + endState
							+ " for key " + WORKFLOW_TRANSITION + startState + "." + key
							+ " was not specified in initial state list");
				}

				workflow.put(startState, key, endState);
				actions.put(startState, action);
			}
		}
	}
	
	public void doProcess(Message object) throws Exception {

		String currentState = object.getState();

		GenericNode action = actions.get(currentState);

		if (action == null) {
			throw new RuntimeException("Node not found for state " + currentState);
		}
		
		String transitionCode = action.execute(object);

		if (GenericNode.error.equals(transitionCode)) {
			throw new RuntimeException("Error returned by " + currentState);
		}

		String newState = workflow.get(currentState, transitionCode);

		if (newState == null)
			return;

		object.setState(newState);
	}
}
