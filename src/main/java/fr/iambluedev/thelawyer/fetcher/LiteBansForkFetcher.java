package fr.iambluedev.thelawyer.fetcher;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.iambluedev.thelawyer.model.Hotlink;
import fr.iambluedev.thelawyer.model.Sanction;

public class LiteBansForkFetcher {

	private Logger logger = Logger.getLogger("LiteBansForkFetcher");
	
	public List<Sanction> fetch(Hotlink hotlink, boolean rebuild) throws IOException, ParseException{
		List<Sanction> datas = new ArrayList<Sanction>();
		datas.addAll(this.fetch(hotlink, "ban", 2, rebuild));
		datas.addAll(this.fetch(hotlink, "mute", 1, rebuild));
		datas.addAll(this.fetch(hotlink, "kick", 4, rebuild));
		logger.info("Receiving a total of " + datas.size() + " sanctions for this hotlink");
		return datas;
	}
	
	private List<Sanction> fetch(Hotlink hotlink, String file, Integer type, boolean rebuild) throws IOException, ParseException {
		List<Sanction> datas = new ArrayList<Sanction>();
		logger.info("Fetching " + file + " ...");
		boolean fetchingBans = true;
		int i = 1;
		while(fetchingBans){
			Document doc = Jsoup.connect(hotlink.getBanlist() + "/index.php?p=" + file + "&pageNo=" + i).get();
			Element table = (Element) doc.select("table").get(0);
			Elements rows = table.select("tr");
			if(rows.size() > 2) {
				for(Element row : rows){
					Elements cols = row.select("td");
					if(cols.size() > 0){
						String banned_name = cols.get(0).text();
						String banned_by = cols.get(3).text();
						String ban_reason = cols.get(2).text();
						String ban_at = cols.get(4).text();
						
						if(!banned_name.equals("adresse IP caché") && banned_name.length() < 20) {
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							
						    Date parsedDate = dateFormat.parse(ban_at);
						    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
						    ban_at = timestamp.getTime() / 1000 + "";
						    
						    if(!rebuild){
								if(((System.currentTimeMillis() / 1000) - Long.valueOf(ban_at)) > 86400){
									continue;
								}
							}
						    
						    Sanction sanction = new Sanction();
							sanction.setBan_type(type);
							sanction.setBanned_name(banned_name);
							sanction.setBan_reason((ban_reason.equals("")) ? "no specified reason" : ban_reason);
							sanction.setBanned_by(banned_by);
							sanction.setBan_at(Long.valueOf(ban_at));
							sanction.setHotlink(hotlink);
							datas.add(sanction);
						}
					}
				}
			}else{
				fetchingBans = false;
				break;
			}
			i++;
		}
		logger.info("Receiving a total of " + datas.size() + " " + file);
		return datas;
	}
}
