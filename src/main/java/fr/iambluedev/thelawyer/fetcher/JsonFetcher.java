package fr.iambluedev.thelawyer.fetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import fr.iambluedev.thelawyer.model.Hotlink;
import fr.iambluedev.thelawyer.model.Sanction;
import fr.iambluedev.thelawyer.model.Sanctions;

public class JsonFetcher {

	private Logger logger = Logger.getLogger("JsonFetcher");
	
	public List<Sanction> fetch(Hotlink hotlink, boolean rebuild){
		List<Sanction> datas = new ArrayList<Sanction>();
		
		HttpRequest request = HttpRequest.get(hotlink.getBanlist());
		request.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0");
		request.followRedirects(true);
		request.trustAllCerts();
		request.trustAllHosts();
		request.acceptGzipEncoding().uncompress(true);
		String response = request.body();
		
		logger.info("Receiving and formatting response ...");
		Sanctions sanctions = new Gson().fromJson(response, Sanctions.class);
		logger.info("Receiving a total of " + sanctions.getData().size() + " sanctions");
		
		for(int i = 0; i < sanctions.getData().size(); i++){
			@SuppressWarnings("unchecked")
			ArrayList<String> tmp = (ArrayList<String>) sanctions.getData().get(i);
			
			if(!rebuild){
				if(((System.currentTimeMillis() / 1000) - Long.valueOf(tmp.get(4))) > 86400){
					continue;
				}
			}
			
			Sanction sanction = new Sanction();
			sanction.setBan_type(Integer.valueOf(tmp.get(0)));
			sanction.setBanned_name(tmp.get(1));
			sanction.setBan_reason(tmp.get(2));
			sanction.setBanned_by(tmp.get(3));
			sanction.setBan_at(Long.valueOf(tmp.get(4)));
			sanction.setHotlink(hotlink);
			datas.add(sanction);
		}
		
		return datas;
	}
}
