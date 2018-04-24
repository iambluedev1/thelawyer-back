package fr.iambluedev.thelawyer.fetcher;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.iambluedev.thelawyer.model.Hotlink;
import fr.iambluedev.thelawyer.model.Sanction;

public class LiteBansFetcher {

	private Logger logger = Logger.getLogger("LiteBansFetcher");
	
	public List<Sanction> fetch(Hotlink hotlink, boolean rebuild) throws IOException, ParseException{
		List<Sanction> datas = new ArrayList<Sanction>();
		datas.addAll(this.fetch(hotlink, "bans", 2, rebuild));
		datas.addAll(this.fetch(hotlink, "mutes", 1, rebuild));
		datas.addAll(this.fetch(hotlink, "warnings", 3, rebuild));
		datas.addAll(this.fetch(hotlink, "kicks", 4, rebuild));
		logger.info("Receiving a total of " + datas.size() + " sanctions for this hotlink");
		return datas;
	}
	
	private List<Sanction> fetch(Hotlink hotlink, String file, Integer type, boolean rebuild) throws IOException, ParseException {
		List<Sanction> datas = new ArrayList<Sanction>();
		logger.info("Fetching " + file + " ...");
		boolean fetchingBans = true;
		int i = 1;
		while(fetchingBans){
			Document doc = Jsoup.connect(hotlink.getBanlist() + "/" + file + ".php?page=" + i).get();
			Element table = (Element) doc.select("table").get(0);
			Elements rows = table.select("tr");
			if(rows.size() > 0) {
				for(Element row : rows){
					Elements cols = row.select("td");
					if(cols.size() > 0){
						String banned_name = cols.get(0).getElementsByTag("p").text();
						String banned_by = cols.get(1).getElementsByTag("p").text();
						String ban_reason = cols.get(2).getElementsByTag("a").html();
						String ban_at = cols.get(3).getElementsByTag("a").html();
						ban_at = ban_at.replace(" (Expiré)", "").replace("Decembre", "Décembre").replace("Aout", "Août").replace(",", "");
						
						String[] tmp = ban_at.split(" ");
						ban_at = tmp[0] + "/" + tmp[1] + "/" + tmp[2] + "-" + ((tmp.length > 4) ? tmp[4] : tmp[3]);
						
						SimpleDateFormat dateFormat = null;
						if(hotlink.getName().equals("bloodsymphony")) {
							dateFormat = new SimpleDateFormat("dd/MMMM/yyyy-hh:mm", Locale.US);
						}else{
							dateFormat = new SimpleDateFormat("dd/MMMM/yyyy-hh:mm", Locale.FRANCE);
						}
						
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
