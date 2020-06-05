package com.code2020.joker.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.code2020.joker.kit.HttpClientKit;
import com.code2020.joker.model.Joke;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

@Component
public class JokeSpiderComponent {

    private static Logger logger = LoggerFactory.getLogger(JokeSpiderComponent.class);

    private static final String JOKE_DEAL_BASE_URL = "https://www.jj00.cn";
    private static final String JOKE_DEAL_URL_PRE = "https://www.jj00.cn";
    private static final String JOKE_NEED_DEAL_LIST = "JOKE_NEED_DEAL_LIST";
    private static final String JOKE_NEED_DEAL_ITEM = "JOKE_NEED_DEAL_ITEM";
    private static final String JOKE_DEAL_LIST = "JOKE_DEAL_LIST";
    private static final String JOKE_DEAL_ITEM = "JOKE_DEAL_ITEM";
    private static final String JOKE_MAP = "JOKE_MAP";
    private static final String JOKE_ID = "JOKE_ID";


    @Autowired
    private StringRedisTemplate template;

    public void spiderType(String url) throws IOException {
        String baseBody = HttpClientKit.getBody(JOKE_DEAL_BASE_URL);
        Document baseDocument = Jsoup.parse(baseBody);
        Elements jokeTypeElements = baseDocument.select(".url5").select("a");
        Iterator<Element> jokeTypeElementsIterator = jokeTypeElements.listIterator();
        while(jokeTypeElementsIterator.hasNext()){
            Element typeItem = jokeTypeElementsIterator.next();
            template.opsForSet().add(JOKE_NEED_DEAL_LIST , typeItem.attr("href"));
        }
    }

    public void spider(String url) throws IOException {
        Set<String> doDealTypes = template.opsForSet().members(JOKE_NEED_DEAL_LIST);
        Iterator<String> i = doDealTypes.iterator();
        while(i.hasNext()) {
            String doDealType = i.next();

            if (template.opsForSet().isMember(JOKE_DEAL_LIST,doDealType)){
                continue;
            }
            logger.info(doDealType);

            String doDealTypeBody = HttpClientKit.getBody(JOKE_DEAL_URL_PRE + doDealType);
            Document document = Jsoup.parse(doDealTypeBody);

            Elements jokeItem = document.select(".mx2").select("a");
            logger.info(jokeItem.toString());

            Iterator<Element> jokeItemIterator = jokeItem.listIterator();
            while (jokeItemIterator.hasNext()) {
                Element typeItem = jokeItemIterator.next();
//                System.out.println(typeItem.attr("href"));
//                logger.info(typeItem.attr("href"));
                template.opsForSet().add(JOKE_NEED_DEAL_ITEM, typeItem.attr("href"));
            }
            template.opsForSet().add(JOKE_DEAL_LIST,doDealType);
        }
    }

    public void spiderContent(String url) throws IOException {
        Set<String> doDealTypes = template.opsForSet().members(JOKE_NEED_DEAL_ITEM);
        Iterator<String> i = doDealTypes.iterator();
        while(i.hasNext()) {
            String doDealType = i.next();

            if (template.opsForSet().isMember(JOKE_DEAL_ITEM,doDealType)){
                continue;
            }

            String doDealTypeBody = HttpClientKit.getBody(JOKE_DEAL_URL_PRE + doDealType);
            Document document = Jsoup.parse(doDealTypeBody);

            String title = document.select(".titleview").text();
            String content = document.select("div.content").text();
            String finalContent = content.substring(content.indexOf("]") == -1 ? 0 : content.indexOf("]"));
            String subtitleview = document.select(".subtitleview").text();
            Joke joke = new Joke();
            Long id = template.opsForValue().increment(JOKE_ID);
            joke.setDealDate(new Date());
            joke.setContent(finalContent);
            joke.setTitle(title);
            joke.setId(id);
            joke.setDealDate(new Date());
            joke.setPublishDate(subtitleview);

            template.opsForHash().put(JOKE_MAP,String.valueOf(id), JSONObject.toJSONString(joke));
        }
    }

}
