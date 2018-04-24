package fr.iambluedev.thelawyer.fetcher;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.iambluedev.cfscrape.core.CfScrape;
import fr.iambluedev.thelawyer.App;
import fr.iambluedev.thelawyer.model.Hotlink;
import fr.iambluedev.thelawyer.model.Sanction;

public class PersonalFetcher {

	private Logger logger = Logger.getLogger("PersonalFetcher");
	
	public List<Sanction> fetch(Hotlink hotlink, boolean rebuild) throws IOException, ParseException {
		List<Sanction> datas = new ArrayList<Sanction>();
		
		CfScrape cfScrape = CfScrape.get();
		cfScrape.setUrl(hotlink.getBanlist());
		cfScrape.setApiHost(App.getInstance().getWebConfig().getCfscrape_url());
		String html = cfScrape.getSyncHtml();
		
		Document doc = Jsoup.parse(html);
		Element table = (Element) doc.select("table").get(0);
		Elements rows = table.select("tr");
		if(rows.size() > 0) {
			for(Element row : rows){
				Elements cols = row.select("td");
				if(cols.size() > 0) {
					String banned_name = cols.get(0).getElementsByTag("a").text();
					String ban_reason = cols.get(1).text();
					String banned_by = cols.get(2).getElementsByTag("a").text();
					String ban_at = cols.get(3).attr("data-order").replace(".", "-").split("-")[0];
					
					if(!rebuild){
						if(((System.currentTimeMillis() / 1000) - Long.valueOf(ban_at)) > 86400){
							continue;
						}
					}
					
					Sanction sanction = new Sanction();
					sanction.setBan_type(2);
					sanction.setBanned_name(banned_name);
					sanction.setBan_reason((ban_reason.equals("")) ? "no specified reason" : ban_reason);
					sanction.setBanned_by(banned_by);
					sanction.setBan_at(Long.valueOf(ban_at));
					sanction.setHotlink(hotlink);
					datas.add(sanction);
				}
			}
		}
		logger.info("Receiving a total of " + datas.size() + " bans");
		return datas;
	}
}
