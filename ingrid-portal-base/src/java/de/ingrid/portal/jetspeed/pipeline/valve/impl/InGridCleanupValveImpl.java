/*
 * **************************************************-
 * Ingrid Portal Base
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/**
 * 
 */
package de.ingrid.portal.jetspeed.pipeline.valve.impl;

import java.util.List;

import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.pipeline.valve.impl.CleanupValveImpl;
import org.apache.jetspeed.request.RequestContext;

/**
 * 
 * The class cleans rendered content including render-writer-buffers are hanging 
 * on content fragment definition. 
 * 
 * It does not clean renderer cache as soon as content fragments are stored 
 * separately.
 * 
 * 
 * Application server threads are smaller after work theis cleaner. 
 * 
 */
public class InGridCleanupValveImpl extends CleanupValveImpl {


	/* (non-Javadoc)
	 * @see org.apache.jetspeed.pipeline.valve.impl.CleanupValveImpl#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
	 */
	public void invoke(RequestContext request, ValveContext context) throws PipelineException {
		super.invoke(request, context);
		ContentPage page = request.getPage();
		if (page != null) {
			Fragment f = page.getRootFragment();
			cleanFragmentsTree(f);
		}
	}
	
	/**
	 * Disconnect content fragment from fragment definition.
	 * 
	 * @param f parent fragment
	 */
	private void cleanFragmentsTree(Fragment f) {
		if (f != null) {
			List<Fragment> fragments = (List<Fragment>)f.getFragments();
			if (fragments != null && fragments.size() > 0) {
				//Process child fragments
				for (Fragment child : fragments) {
					cleanFragmentsTree(child);
				}
			}
			//Clean the given fragment
			if (f instanceof ContentFragment) {
				((ContentFragment)f).setPortletContent(null);
			}
		}
	}	
}
