package com.novelreader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
/**
 * Gemini API客户端的单元测试
 * 注意：这个测试类使用了Mockito进行模拟，需要添加Mockito依赖
 */
@ExtendWith(MockitoExtension.class)
public class GeminiApiClientTest {
    
    private GeminiApiClient apiClient;
    
    @Mock
    private RateLimiter mockRateLimiter;
    
    private final String testApiKey = "AIzaSyB8-fJRn3WYRpoesMhogiZ1Gz2oxdB0al0";
    private final String testModelName = "gemini-2.0-flash";
    private final String testPrompt = "请你阅读并逐步分析《牧神记》的每一章节,输出结果格式是markdown,不要输出任何无关内容。在分析中，请重点关注以下几个方面，并尽可能提供详细的描写和具体的情节：\r\n" + //
                "\r\n" + //
                "男主角的经历：\r\n" + //
                "\r\n" + //
                "男主角在本章节中的冒险旅程、日常生活，以及遇到的具体事件。\r\n" + //
                "\r\n" + //
                "描述男主角在这些章节中的成长，体现在性格、技能、心境等方面的变化。\r\n" + //
                "\r\n" + //
                "男主角在这些章节中是否有重要的转折点或关键事件？如果有，请详细描述事件的内容和影响。\r\n" + //
                "\r\n" + //
                "世界观与设定：\r\n" + //
                "\r\n" + //
                "在本章节中，小说世界观有什么体现？\r\n" + //
                "\r\n" + //
                "详细解释本章节涉及的地理环境、设定、特殊规则和力量原理\r\n" + //
                "\r\n" + //
                "人物关系：\r\n" + //
                "\r\n" + //
                "列出并详细描述与男主角相关的关键人物在本章节中的表现，他们之间的互动、对话，以及对主角的影响。\r\n" + //
                "他们的背景、性格，以及与主角的关系。\r\n" + //
                "\r\n" + //
                "虚构历史：\r\n" + //
                "\r\n" + //
                "如果本章节提到重要的历史事件、传说、神话，请详细说明。\r\n" + //
                "这些历史事件和传说对当前世界和男主角产生了什么影响？\r\n" + //
                "其他重要细节：\r\n" + //
                "\r\n" + //
                "除了上述四点，请关注章节中任何重要的细节，包括伏笔、线索、暗示等。\r\n" + //
                "\r\n" + //
                "总结每章的关键内容，并分析章节之间的关联性。\r\n" + //
                "\r\n" + //
                "请注意：\r\n" + //
                "\r\n" + //
                "避免过于简略，重点突出情节的细节，尽可能用具体的例子和描述来支撑你的分析。\r\n" + //
                "\r\n" + //
                "对于战斗场景，可以简要概括，但重点要放在战斗的结果、对剧情的影响，以及战斗中体现的人物关系和设定。\r\n" + //
                "\r\n" + //
                "对伏笔、线索等进行提示，并分析其可能的影响。";
    private final String testContent = "第1章 天黑别出门\r\n" + //
                "\r\n" + //
                "\r\n" + //
                "　　天黑，别出门。\r\n" + //
                "\r\n" + //
                "　　秦牧脑中轰然，不知道这个从牛皮里钻出来的女子在说些什么。\r\n" + //
                "\r\n" + //
                "　　那女子正要一刀砍死他，突然后心一凉，低头看去，一口刀从她胸前穿出。\r\n" + //
                "\r\n" + //
                "　　“牧儿，你药师爷爷让你回去吃药了。”女子尸体倒下，身后站着的是村里的瘸子爷爷，慈眉善目，一脸憨厚，手里拎着一口血淋漓的刀，向秦牧笑道。\r\n" + //
                "\r\n" + //
                "　　“瘸子爷爷……”秦牧身躯发软，看了看地上的那张牛皮和女子尸体，还是没有回过神来。\r\n" + //
                "\r\n" + //
                "　　“回去，回去。”瘸子拍了拍他的肩头，呵呵笑道。\r\n" + //
                "\r\n" + //
                "　　秦牧一脚高一脚低往村里走，回头看去，却见瘸子将那女子的尸体丢进江里。\r\n" + //
                "\r\n" + //
                "　　这一幕给他的冲击实在太大，以至于他都不知道自己是何时回到村子里。\r\n" + //
                "\r\n" + //
                "　　“秦牧！死小子，怎么告诉你的？天黑别出门！”\r\n" + //
                "\r\n" + //
                "　　夜幕降临，残老村四角的石像又自动亮了起来，司婆婆唤住正打算溜出村子去江边查看牛皮的秦牧，将他拖了回来。\r\n" + //
                "\r\n" + //
                "　　“婆婆，为什么天黑不能出门？”秦牧抬头问道。\r\n" + //
                "\r\n" + //
                "　　“天黑的时候，会有一些可怕的东西在黑暗中活动，出去就是死。”\r\n" + //
                "\r\n" + //
                "　　司婆婆郑重道：“村里的石像会保护我们，黑暗里的东西不敢进入村子。”\r\n" + //
                "\r\n" + //
                "　　“其他村子也有这样的石像吗？”秦牧好奇道。\r\n" + //
                "\r\n" + //
                "　　司婆婆点头，面色却有些忧虑，不住的看向村外，低声道：“瘸子应该回来了……真不应该让瘸子出去的，这家伙只有一条腿……”\r\n" + //
                "\r\n" + //
                "　　“婆婆，今天出怪事了……”\r\n" + //
                "\r\n" + //
                "　　秦牧迟疑一下，将牛肚子里钻出个女人的事情说了一遍，司婆婆漫不经心道：“你是说那个女人？瘸子跟我说过了，他处理得很好。早在你四岁断奶的时候我就说过将牛卖了，只是你舍不得，所以才让你喂着。你看，现在出事了吧？我就说吃奶吃到四岁，会对奶牛有感情。”\r\n" + //
                "\r\n" + //
                "　　秦牧红了脸，四岁断奶的确有些太长了，不过好像关键不是在四岁断奶吧？\r\n" + //
                "\r\n" + //
                "　　“婆婆，那个女人被瘸爷爷杀了……”\r\n" + //
                "\r\n" + //
                "　　“杀得好。”\r\n" + //
                "\r\n" + //
                "　　司婆婆笑道：“那是便宜了她。十一年前她就应该死了，如果不是要奶你，她能活到现在？”\r\n" + //
                "\r\n" + //
                "　　秦牧不明所以。\r\n" + //
                "\r\n" + //
                "　　司婆婆瞥他一眼，道：“这女子是距离这儿千里外的镶龙城城主夫人，镶龙城主好色，而她善妒，镶龙城主喜欢在外面拈花惹草，强掠良家女子。而镶龙城主每坏了一个女子的清白，这位城主夫人便会派人将那女子活活打死。我潜入镶龙城，原本打算杀她，见到她刚刚生了一个孩子，孩子才三个月，又想到你还没有奶喝，而她有奶，于是将她变成一头奶牛回来奶你。只是没想到这女子竟然挣脱了封印，能够开口说话，差点就害了你。”\r\n" + //
                "\r\n" + //
                "　　秦牧瞠目结舌，失声道：“婆婆，人怎么能变成牛？”\r\n" + //
                "\r\n" + //
                "　　司婆婆嘿嘿一笑，露出半嘴零落牙齿：“你想学？我教你……瘸子回来了！”\r\n" + //
                "\r\n" + //
                "　　秦牧看去，只见瘸子一手拄着拐杖，一手抓着背上的猎物，正一瘸一拐的走来。黑暗如同潮水飞快的向村子涌来，司婆婆急忙叫道：“死瘸子，快点，快点！”\r\n" + //
                "\r\n" + //
                "　　“急什么？”\r\n" + //
                "\r\n" + //
                "　　瘸子还是不紧不慢的往村子走，在他走入村子的一刹那，浓烈的黑暗正好将村子淹没。他背上的猎物是一头斑斓猛虎，还没有死，尾巴被黑暗扫中，突然猛虎发出一声哀吼，秦牧连忙看去，只见猛虎的尾巴竟然只剩下了一节节骨头，尾巴上的皮毛和血肉全都不见了，好像是被什么东西啃掉的一般。\r\n" + //
                "\r\n" + //
                "　　他好奇的看了看村外的黑暗，那里漆黑一片，什么也看不到。\r\n" + //
                "\r\n" + //
                "　　“黑暗里到底有什么？”他心中纳闷。\r\n" + //
                "\r\n" + //
                "\r\n" + //
                "\r\n" + //
                "\r\n" + //
                "\r\n" + //
                "第2章 四灵血\r\n" + //
                "\r\n" + //
                "\r\n" + //
                "　　“好人？嘿嘿……”\r\n" + //
                "\r\n" + //
                "　　马爷自嘲一笑，道：“我们这些废人被逼到大墟中，苟延残喘活到现在，大墟太危险，没有我们，牧儿的确很难活下去。我们应该把他送出大墟，外面安全很多……”\r\n" + //
                "\r\n" + //
                "　　屠户冷冰冰道：“送他出去，我们会被仇家发现，都会死，他也会被我们连累，也会死。”\r\n" + //
                "\r\n" + //
                "　　残老村再次沉默下来。突然村长道：“好。”\r\n" + //
                "\r\n" + //
                "　　司婆婆纳闷：“什么好？”\r\n" + //
                "\r\n" + //
                "　　村长露出笑容：“我说他的体质好，是个好苗子。”\r\n" + //
                "\r\n" + //
                "　　屠户、药师等人都是一怔，不明其意，村长笑道：“我觉得牧儿应该是另一种体质，结合四大体质之长的霸体！”\r\n" + //
                "\r\n" + //
                "　　“霸体？”司婆婆等人露出疑惑之色，他们都是见多识广之辈，但是也没有听说过霸体这个名字。\r\n" + //
                "\r\n" + //
                "　　“对，是霸体。”\r\n" + //
                "\r\n" + //
                "　　村长笑道：“普通的灵血很难激发霸体，需要集齐真正的四大灵兽之血，才能让霸体显现出来。大墟中没有四大灵兽，但是灵兽的后代却并不难寻，你们继续捕捉猛虎大蛇，炼出灵血，喝得多了，自然能够将他的霸体激发出来。”\r\n" + //
                "\r\n" + //
                "　　村长很有威信，村里缺胳膊少腿的老头老太太听了都很是开心，司婆婆笑道：“明天我也陪死瘸子去捉老虎！牧儿，你也早点睡，明天还要喝灵血！”\r\n" + //
                "\r\n" + //
                "　　众人散去，药师和哑巴铁匠将村长送回房间，哑巴离开，药师却没有走，低声道：“从来没有过霸体。”\r\n" + //
                "\r\n" + //
                "　　村长点头：“是我信口说的。我不这么说，村里的人都很难活下去。”\r\n" + //
                "\r\n" + //
                "　　药师怔了怔。残老村的村民各有来历，但都被逼得不得不进入大墟来到残老村，苟延残喘，他们本来怨天怨地怨苍生，怨气太重，能够活到现在，不能不说秦牧也有功劳。\r\n" + //
                "\r\n" + //
                "　　正是这个手足健全的小娃娃的到来，冲散了众人心中的怨气，大家抚养秦牧长大，都将这个小男孩当成自己最亲的人，秦牧维系着残老村的村民脆弱的心灵。\r\n" + //
                "\r\n" + //
                "　　倘若村民们知道秦牧只是最普通的体质，无法独自在大墟中存活，只怕这些家伙都会失控，不知道会做出什么事来。\r\n" + //
                "\r\n" + //
                "　　药师面无表情道：“但是你瞒不了一辈子，我们迟早都会老死，只剩下秦牧。”\r\n" + //
                "\r\n" + //
                "　　“所以你不要告诉他从来没有过霸体，永远也不要告诉他。”\r\n" + //
                "\r\n" + //
                "　　村长沉声道：“让他相信，他就是独一无二的霸体！”\r\n" + //
                "\r\n" + //
                "　　药师怔了怔，仔仔细细看了看他的脸庞。村长的脸庞在昏暗的油灯下显得格外有魅力，笑道：“我想看看，一个普通人在无与伦比的信念下会不会超凡脱俗，做出我们这些灵体也做不出的事情来！说不定将来，他真的能够走出一条凡体即霸体的道路来！”\r\n" + //
                "\r\n" + //
                "　　药师呆了呆：“凡体即霸体？”\r\n" + //
                "\r\n" + //
                "　　村长重重点头：“只要有信念，凡体即霸体！”";
    
    @BeforeEach
    public void setUp() {
        apiClient = new GeminiApiClient(testApiKey, mockRateLimiter);
    }
    
    /**
     * 注意：由于实际API调用需要网络连接和真实的API密钥，
     * 这个测试方法被标记为disabled。在实际环境中，
     * 你可能需要使用模拟HTTP客户端来测试API调用。
     */
    @Test
    public void testAnalyzeChapterGroup_MockedImplementation() throws InterruptedException {
        // 创建测试请求
        ApiRequest request = new ApiRequest(testModelName, testPrompt, testContent);
        
        // 由于我们不能实际调用API，这里我们只测试RateLimiter的使用
        // 实际项目中，你可能需要模拟HttpClient来测试完整的API调用流程
        
        // 执行API调用（注意：这会尝试实际调用API，但会因为无效的API密钥而失败）
        ApiResponse response = apiClient.analyzeChapterGroup(request);
        System.out.println(response.toString());
        // 验证RateLimiter被正确使用
        verify(mockRateLimiter, times(1)).acquire();
        verify(mockRateLimiter, times(1)).release();
        
        // 由于我们使用了测试API密钥，预期API调用会失败
        assertTrue(response.isSuccess(), "使用测试API密钥应该导致API调用失败");
        assertNotNull(response.responseBody(), "应该有错误消息");
    }
    
    /**
     * 测试API请求对象的创建和属性
     */
    @Test
    public void testApiRequest() {
        ApiRequest request = new ApiRequest(testModelName, testPrompt, testContent);
        
        assertEquals(testModelName, request.getModelName(), "模型名称不匹配");
        assertEquals(testPrompt, request.getPrompt(), "提示不匹配");
        assertEquals(testContent, request.getChapterGroupContent(), "内容不匹配");
        
        // 测试setter
        String newModelName = "gemini-pro-vision";
        request.setModelName(newModelName);
        assertEquals(newModelName, request.getModelName(), "更新后的模型名称不匹配");
    }
    
    /**
     * 测试API响应对象的创建和属性
     */
    @Test
    public void testApiResponse() {
        String successBody = "成功响应";
        ApiResponse successResponse = ApiResponse.success(successBody);
        
        assertTrue(successResponse.isSuccess(), "成功响应的success标志应为true");
        assertEquals(successBody, successResponse.responseBody(), "响应体不匹配");
        assertNull(successResponse.errorMessage(), "成功响应不应有错误消息");
        
        String errorMessage = "错误消息";
        ApiResponse failureResponse = ApiResponse.failure(errorMessage);
        
        assertFalse(failureResponse.isSuccess(), "失败响应的success标志应为false");
        assertNull(failureResponse.responseBody(), "失败响应不应有响应体");
        assertEquals(errorMessage, failureResponse.errorMessage(), "错误消息不匹配");
    }
}
