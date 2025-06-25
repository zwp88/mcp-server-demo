package com.example.tools;

import com.example.utils.IPUtils;
import com.example.utils.WeatherUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.example.utils.IPLocation.getCityFromIP;

/**
 * @author Administrator
 */
@Slf4j
@Service
public class WeatherService {


    @Tool(description ="获取当前日期")
    public String getCurrentDateTime() {
        log.info("获取当前日期");
        return  LocalDateTime.now().toString();
    }

    @Tool(description ="根据当前城市信息获取当前城市编号")
    public ComResult getCityName(@ToolParam(description="城市名称") String cityName) throws Exception {

        JsonArray location = WeatherUtils.getCityData(cityName).get("location").getAsJsonArray();
        String result = location.get(0).getAsJsonObject().get("id").toString().replace("\"", "");
        log.info("获取城市编号:{}", result);
        return new ComResult(result);
    }

    @Tool(description = "获取当前电脑的公共ip")
    public String currPublicIp(){
        try {
            log.info("获取当前电脑的公共ip");
            return IPUtils.getPublicIP();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Tool(description = "根据当前ip获取当前城市的拼音或者英文单词")
    public String currCity(@ToolParam(description="当前ip") String ip)  {
        log.info("============获取当前城市{}",ip);
        String publicIp = null;
        try {
            String result = Objects.requireNonNull(getCityFromIP(ip)).get("city").getAsString();
            log.info("城市信息：" + result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Tool(description ="获取当前位置经纬度")
    public String currLatAndLon() throws IOException {
        log.info("获取经纬度");
        String publicIP = IPUtils.getPublicIP();
        JsonObject obj = getCityFromIP(publicIP);
        assert obj != null;
        return obj.get("lat").getAsString() + "," + obj.get("lon").getAsString();
    }


    @Tool(description ="根据当前城市信息获取当前城市编号")
    public String getCityNo(@ToolParam(description="城市拼音或英文单词") String cityName) throws Exception {
        assert cityName!=null;
        JsonArray location = WeatherUtils.getCityData(cityName.toUpperCase()).get("location").getAsJsonArray();
        String result = location.get(0).getAsJsonObject().get("id").toString().replace("\"", "");
        log.info("获取城市编号:" + result);
        return result;
    }

    public static void main(String[] args) throws Exception {
        WeatherService weatherService = new WeatherService();
        System.out.println(weatherService.currPublicIp());
        System.out.println(weatherService.currCity(weatherService.currPublicIp()));
        System.out.println(weatherService.getCityNo(weatherService.currCity(weatherService.currPublicIp())));
        System.out.println(weatherService.getWeather(weatherService.getCityNo(weatherService.currCity(weatherService.currPublicIp()))));
    }


    @Tool(description ="根据城市编号获取当前的天气" ,returnDirect = false)
    public String getWeather(@ToolParam(description="城市编号") String no) throws Exception {
        log.info("当前城市编号：" + no);
        String result = Objects.requireNonNull(WeatherUtils.getCurrWeatherByLocation(no)).get("now").toString();
        log.info("当地天气：" + result);
        result += """
                返回参数说明
                                    
                obsTime 数据观测时间
                temp 温度，默认单位：摄氏度
                feelsLike 体感温度，默认单位：摄氏度
                icon 天气状况的图标代码，另请参考天气图标项目
                text 天气状况的文字描述，包括阴晴雨雪等天气状态的描述
                wind360 风向360角度
                windDir 风向
                windScale 风力等级
                windSpeed 风速，公里/小时
                humidity 相对湿度，百分比数值
                precip 过去1小时降水量，默认单位：毫米
                pressure 大气压强，默认单位：百帕
                vis 能见度，默认单位：公里
                cloud 云量，百分比数值
                """;
        return result;
    }

   public record ComResult(String result) {

    }
}
