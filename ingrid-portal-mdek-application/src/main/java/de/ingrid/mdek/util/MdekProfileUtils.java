package de.ingrid.mdek.util;

import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.beans.ProfileBean;
import de.ingrid.mdek.beans.Rubric;
import de.ingrid.mdek.beans.controls.Controls;

/**
 * Helper class for manipulating profile. USED IN IMPORTER !!!!!!!
 * @author martin
 */
public class MdekProfileUtils {

	private final static Logger log = Logger.getLogger(MdekProfileUtils.class);
	
	/** Returns null if rubric not found */
	public static Rubric findRubric(ProfileBean profileBean, String rubricId) {
		Rubric retValue = null;

        for (Rubric rubric : profileBean.getRubrics()) {
			if (rubricId.equals(rubric.getId())) {
				retValue = rubric;
				break;
			}
        }

		return retValue;
	}

	/** Returns null if rubric not found */
	public static Integer findRubricIndex(ProfileBean profileBean, String rubricId) {
		Integer retValue = null;

		List<Rubric> rubrics = profileBean.getRubrics();
		for (int i=0; i < rubrics.size(); i++) {
			if (rubricId.equals(rubrics.get(i).getId())) {
				retValue = i;
				break;
			}
		}
		
		return retValue;
	}

	/** Returns null if control not found */
	public static Rubric findRubricOfControl(ProfileBean profileBean, String controlId) {
		Rubric retValue = null;

        for (Rubric rubric : profileBean.getRubrics()) {
            for (Controls control : rubric.getControls()) {
    			if (controlId.equals(control.getId())) {
    				retValue = rubric;
    				break;
    			}
            }
        }

		return retValue;
	}

	/** Returns null if rubric not found */
	public static Rubric removeRubric(ProfileBean profileBean, String rubricId) {
		Rubric retValue = null;

		int index = findRubricIndex(profileBean, rubricId);
		if (index > -1) {
			retValue = profileBean.getRubrics().remove(index);
		}
		
		return retValue;
	}

	/** Add given rubric add given position (index, starts at 0)  */
	public static void addRubric(ProfileBean profileBean, Rubric rubric, int index) {
		profileBean.getRubrics().add(index, rubric);
	}

	/** Returns null if control not found. */
	public static Controls findControl(ProfileBean profileBean, String controlId) {
		Controls retValue = null;

        for (Rubric rubric : profileBean.getRubrics()) {
            for (Controls control : rubric.getControls()) {
    			if (controlId.equals(control.getId())) {
    				retValue = control;
    				break;
    			}
            }
        }

		return retValue;
	}

	/** Determine index of control in given rubric of control. Returns null if control not found */
	public static Integer findControlIndex(ProfileBean profileBean, Rubric rubric, String controlId) {
		Integer retValue = null;

		List<Controls> controls = rubric.getControls();
		for (int i=0; i < controls.size(); i++) {
			if (controlId.equals(controls.get(i).getId())) {
				retValue = i;
				break;
			}
		}
	
		return retValue;
	}

	/** Returns null if control not found */
	public static Controls removeControl(ProfileBean profileBean, String controlId) {
		Controls retValue = null;

		Rubric rubric = findRubricOfControl(profileBean, controlId);
		int index = findControlIndex(profileBean, rubric, controlId);
		if (index > -1) {
			retValue = rubric.getControls().remove(index);
		}
		
		return retValue;
	}

	/** Add given control in given rubric at given position (index, starts at 0) in given rubric */
	public static void addControl(ProfileBean profileBean, Controls control, Rubric rubric, int index) {
		rubric.getControls().add(index, control);
	}

	public static void updateScriptedProperties(Controls control, String jsToAdd) {
		String js = control.getScriptedProperties();
		control.setScriptedProperties(js + jsToAdd);
	}
}
