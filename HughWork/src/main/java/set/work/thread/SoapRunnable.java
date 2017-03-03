package set.work.thread;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Iterator;
import java.util.Map;

import set.work.bean.RequestListBean;
import set.work.bean.ResultBean;
import set.work.handler.ListenHandler;

public class SoapRunnable extends BaseRequestRunnable {
	private String name_space;
//	private String service_api;
	protected String methodName;
	protected String urlString;
	public SoapRunnable(String namespace, String methodName, ListenHandler handler, RequestListBean requestBean,String urlString) {
		super(handler, requestBean);
		name_space = namespace;
//		service_api = serviceApi;
		this.methodName = methodName;
		this.urlString = urlString;
	}
	
	@Override
	public void processRunnable(ResultBean bean) throws Exception {
		String SOAP_ACTION = name_space + methodName;
		SoapObject request = new SoapObject(name_space, methodName);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = request;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		envelope.encodingStyle = "UTF-8";
//		request.addProperty("ParamName", methodName);
		if (requestBean != null) {
			Iterator iter = requestBean.getSingleMap().entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				request.addProperty(key.toString(), val);
			}
		}
//		if (requestBean != null)
//			request.addProperty("ParamValue", requestBean.toString());
		HttpTransportSE androidHttpTrandsport = new HttpTransportSE(urlString, timeout);
		androidHttpTrandsport.call(SOAP_ACTION, envelope);
		if (envelope.getResponse() != null) {
			SoapObject soapObject = (SoapObject) envelope.bodyIn;
			String returnString = soapObject.getProperty(0).toString();
			bean.setResultString(returnString);
		}
	}

}
