package de.ingrid.mdek.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.mdek.DataMapperInterface;
import de.ingrid.mdek.MdekError;
import de.ingrid.mdek.MdekError.MdekErrorType;
import de.ingrid.mdek.beans.address.MdekAddressBean;
import de.ingrid.mdek.beans.object.MdekDataBean;
import de.ingrid.mdek.exception.EntityReferencedException;
import de.ingrid.mdek.job.MdekException;
import de.ingrid.mdek.job.repository.IJobRepository;
import de.ingrid.utils.IngridDocument;

public class MdekErrorUtils {

	private final static Logger log = Logger.getLogger(MdekErrorUtils.class);

	// Injected via Spring
	private static DataMapperInterface dataMapper;


	public static void handleError(IngridDocument response) throws RuntimeException {
		String errorMessage = getErrorMsgFromResponse(response);
		log.debug(errorMessage);
		List<MdekError> err = getErrorsFromResponse(response);
		if (err != null) {
			if (containsErrorType(err, MdekErrorType.ENTITY_REFERENCED_BY_OBJ)) {
				handleEntityReferencedByObjectError(err);
			} else {
				throw new MdekException(err);
			}
		} else if (errorMessage != null){
			throw new RuntimeException(errorMessage);
		} else {
			return;
		}
	}

	private static void handleEntityReferencedByObjectError(List<MdekError> errorList) {
		MdekAddressBean targetAddress = null;
		MdekDataBean targetObject = null;
		ArrayList<MdekAddressBean> sourceAddresses = new ArrayList<MdekAddressBean>();
		ArrayList<MdekDataBean> sourceObjects = new ArrayList<MdekDataBean>();

		for (MdekError mdekError : errorList) {
			if (mdekError.getErrorType().equals(MdekErrorType.ENTITY_REFERENCED_BY_OBJ)) {
				// In the case of this exception, we have to build an MdekAppException containing additional data
				IngridDocument errorInfo = mdekError.getErrorInfo();
				ArrayList<MdekDataBean> objs = MdekObjectUtils.extractDetailedObjects(errorInfo);
				ArrayList<MdekAddressBean> adrs = MdekAddressUtils.extractDetailedAddresses(errorInfo);
				sourceObjects.addAll(objs);
				sourceAddresses.addAll(adrs);

				targetAddress = dataMapper.getDetailedAddressRepresentation(errorInfo);
			}
		}

		EntityReferencedException e = new EntityReferencedException(MdekErrorType.ENTITY_REFERENCED_BY_OBJ.toString());
		e.setTargetAddress(targetAddress);
		e.setTargetObject(targetObject);
		e.setSourceAddresses(sourceAddresses);
		e.setSourceObjects(sourceObjects);
		throw e;
	}

	private static boolean containsErrorType(List<MdekError> errorList, MdekErrorType errorType) {
		for (MdekError mdekError : errorList) {
			if (mdekError != null && mdekError.getErrorType().equals(errorType))
				return true;
		}
		return false;
	}
	
	private static List<MdekError> getErrorsFromResponse(IngridDocument mdekResponse) {
		return (List<MdekError>) mdekResponse.get(IJobRepository.JOB_INVOKE_ERROR_MDEK);
	}

	private static String getErrorMsgFromResponse(IngridDocument mdekResponse) {
		int numErrorTypes = 4;
		String[] errMsgs = new String[numErrorTypes];

		errMsgs[0] = (String) mdekResponse.get(IJobRepository.JOB_REGISTER_ERROR_MESSAGE);
		errMsgs[1] = (String) mdekResponse.get(IJobRepository.JOB_INVOKE_ERROR_MESSAGE);
		errMsgs[2] = (String) mdekResponse.get(IJobRepository.JOB_COMMON_ERROR_MESSAGE);
		errMsgs[3] = (String) mdekResponse.get(IJobRepository.JOB_DEREGISTER_ERROR_MESSAGE);

		String retMsg = null;
		for (String errMsg : errMsgs) {
			if (errMsg != null) {
				if (retMsg == null) {
					retMsg = errMsg;
				} else {
					retMsg += "\n!!! Further Error !!!:\n" + errMsg;
				}
			}
		}
		
		return retMsg;
	}

	public static RuntimeException convertToRuntimeException(MdekException e) {
		String errorStr = "";
		List<MdekError> errorList = e.getMdekErrors();
		for (MdekError mdekError : errorList) {
			errorStr += mdekError.toString()+" ";
		}
		return new RuntimeException(errorStr.trim());
	}

	public DataMapperInterface getDataMapper() {
		return dataMapper;
	}

	public void setDataMapper(DataMapperInterface dataMapper) {
		MdekErrorUtils.dataMapper = dataMapper;
	}
/*
	public MdekAddressUtils getMdekAddressUtils() {
		return mdekAddressUtils;
	}

	public void setMdekAddressUtils(MdekAddressUtils mdekAddressUtils) {
		this.mdekAddressUtils = mdekAddressUtils;
	}

	public MdekObjectUtils getMdekObjectUtils() {
		return mdekObjectUtils;
	}

	public void setMdekObjectUtils(MdekObjectUtils mdekObjectUtils) {
		this.mdekObjectUtils = mdekObjectUtils;
	}
*/
}
