package com.novelreader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ResultValidator的单元测试
 */
public class ResultValidatorTest {
    
    private ResultValidator validator;
    
    @BeforeEach
    public void setUp() {
        validator = new ResultValidator();
    }
    
    @Test
    public void testValidateResult_WithCorrectChapterCount() {
        // 准备测试数据
        String markdownResult = """
                好的，我将按照您提出的要求，对《牧神记》的第一章和第二章进行详细分析。
                
                **第1章 天黑别出门**
                
                * **男主角的经历：**
                  * 秦牧在本章中还是一个婴儿，被司婆婆在江边捡到，由残老村的村民抚养。
                
                **第2章 四灵血**
                
                * **男主角的经历：**
                  * 秦牧在本章中接受了四灵血的测试，试图激发体内的灵体。
                """;
        
        // 执行验证
        boolean result = validator.validateResult(markdownResult, 2);
        
        // 断言结果
        assertTrue(result, "章节数量正确时应返回true");
    }
    
    @Test
    public void testValidateResult_WithIncorrectChapterCount() {
        // 准备测试数据
        String markdownResult = "## 《牧神记》章节分析：\\n\\n**总览：** 第991章-1000章 可以看作是一个小高潮的收尾，主要围绕秦牧解决无忧乡问题，以及进入天庭后的初步行动展开。核心矛盾是“秦牧如何打破开皇和造物主群体对‘无忧乡’的执念，让他们走出舒适区，迎接挑战”。同时，秦牧在天庭的行动也逐渐展开，开始接触到更深层次的势力和秘密。\\n\\n### 第991章 他们不是你\\n\\n*   **男主角的经历：**\\n\\n    *   秦牧抵达无忧乡，目睹了无忧乡的三种状态：老臣的颓废、新神的享乐、秦家的牺牲。\\n    *   在珍王府祭祖，和秦家后辈见面。\\n    *   和开皇在第三十三重天展开战斗前的对话。\\n*   **男主角的成长：**\\n\\n    *   对“无忧乡”的希望彻底破灭，更加坚定了打破无忧乡现状的决心。\\n    *   对开皇有了更深的了解，知晓其强大和决心，也体会到他背负的压力。\\n*   **男主角的关键事件：**\\n\\n    *   与开皇在凌霄殿前的对话，表明了自己要打破无忧乡现状的决心，为接下来的战斗做铺垫。\\n*   **世界观与设定：**\\n\\n    *   无忧乡的畸形状态：老臣的颓废，新神的享乐，秦家的牺牲，揭示了无忧乡内部的深刻矛盾。\\n    *   开皇天庭的奢华：表明了在无忧乡观想造物的便利性。\\n*   **人物关系：**\\n\\n    *   秦汉珍和珍王妃：对秦牧既有亲情关怀，又有对无忧乡未来的担忧。他们的态度代表了秦家复杂的情感。\\n    *   开皇：秦牧理解开皇的初心和压力，但认为他的方式已经无法解决问题，必须打破现状。\\n    *   开皇的老臣和新神：他们对开皇的态度各不相同，代表了无忧乡不同阶层的想法。\\n    *   叔钧：作为见证者，陪伴秦牧进入开皇天庭。\\n*   **虚构历史：**\\n\\n    *   开皇分割彼岸世界开辟无忧乡的历史，暗示了无忧乡问题的根源。\\n*   **其他重要细节：**\\n\\n    *   秦牧将家乡的土收入秦字大陆中，暗示了自己对根的重视。\\n    *   秦牧与秦思莹的互动，体现了他对秦家后辈的关怀，但同时也暗示了他无法融入秦家的命运。\\n*   **章节总结：** 秦牧来到无忧乡，发现其内部问题重重，与开皇决裂，决心打破无忧乡现状。本章奠定了接下来战斗的基调，并预示着无忧乡即将迎来变革。\\n\\n### 第992章 第三十三重天剑域\\n\\n*   **男主角的经历：**\\n\\n    *   秦牧与开皇展开激烈的战斗，两人施展了开皇的三十三重天剑道。\\n    *   战斗地点从凌霄殿转移到玉京城，甚至扩展到整个太皇天。\\n*   **男主角的成长：**\\n\\n    *   通过与开皇的战斗，重温了开皇时代的精神，理解了开皇变法的初心。\\n    *   意识到自己与开皇在剑道理念上的差异，以及开皇变法失败的原因。\\n*   **男主角的关键事件：**\\n\\n    *   战斗中，开皇天庭中的民众被三十三重天剑道所蕴含的精神唤醒，开始反思自己的现状，为后续无忧乡的变革埋下伏笔。\\n*   **世界观与设定：**\\n\\n    *   开皇剑道三十三重天的详细设定：每一重天都代表着开皇不同时期的剑道理念和精神追求。\\n    *   剑域的设定：太清境道剑代表了剑道领域的形成，与无上神识领域有相似之处，但更注重攻击。\\n*   **人物关系：**\\n\\n    *   开皇：通过战斗，秦牧更加理解开皇，但也更加坚定了要打破他的“无忧乡”的决心。\\n    *   开皇的老臣：三十三重天剑道唤醒了他们沉睡的记忆和激情，使他们重新燃起斗志。\\n    *   烟云兮：对无忧乡的未来感到担忧，希望秦牧能够战胜开皇，但又担心历史重演。\\n*   **虚构历史：**\\n\\n    *   开皇时代的精神：以神为人用为立国之本，以及变法图强的决心。\\n*   **其他重要细节：**\\n\\n    *   炎日暖的热泪：象征着开皇老臣被唤醒的激情。\\n*   **章节总结：** 秦牧和开皇的战斗以三十三重天剑道为主线，唤醒了开皇老臣的激情，为无忧乡的变革埋下伏笔。\\n\\n### 第993章 雄关漫道真如铁\\n\\n*   **男主角的经历：**\\n\\n    *   秦牧打破开皇的剑域，并用天庭的强大力量击溃开皇。\\n    *   羞辱开皇，试图摧毁他的信念。\\n*   **男主角的成长：**\\n\\n    *   对如何唤醒无忧乡的民众有了更深的理解，意识到需要打破他们对开皇的盲目崇拜。\\n*   **男主角的关键事件：**\\n\\n    *   开皇天庭的老臣被秦牧的羞辱所激怒，重新燃起斗志，决心反抗天庭。\\n*   **世界观与设定：**\\n\\n    *   古神天庭的强大：秦牧利用古神的力量，营造出一种压倒性的优势，反衬出开皇的弱小。\\n*   **人物关系：**\\n\\n    *   开皇：秦牧试图用羞辱的方式激怒开皇，打破他的道心，但最终失败。\\n    *   开皇天庭的民众：被秦牧的羞辱所激怒，重新燃起对开皇时代的精神和斗志。\\n    *   珍王妃：担忧秦牧的安危，试图阻止他继续刺激无忧乡的民众。\\n    *   烟云兮：意识到事态超出掌控，担心秦牧被愤怒的民众攻击。\\n*   **虚构历史：**\\n\\n    *   对开皇时代的反思：强调开皇时代并非开皇一个人的时代，而是属于所有参与变法的人。\\n*   **其他重要细节：**\\n\\n    *   战鼓声的象征意义：代表着开皇时代精神的觉醒。\\n*   **章节总结：** 秦牧利用天庭的力量击败开皇，试图摧毁他的信念，但反而激发了开皇天庭民众的反抗精神，为无忧乡的变革奠定了基础。\\n\\n### 第994章 天尊之战，短兵相接\\n\\n*   **男主角的经历：**\\n\\n    *   秦牧受到无忧乡民众的敌视，被珍王妃和烟云兮带离。\\n    *   和开皇在天外进行了一场真正的决战。\\n*   **男主角的成长：**\\n\\n    *   意识到自己无法融入无忧乡，必须离开。\\n    *   通过与开皇的决战，对自己的实力有了更清晰的认知。\\n*   **男主角的关键事件：**\\n\\n    *   和开皇在天外真正的决战，展示了两人真正的实力差距。\\n*   **世界观与设定：**\\n\\n    *   开皇剑二十式的设定：强调了燃烧气血和配合剑域才能施展的特点。\\n*   **人物关系：**\\n\\n    *   珍王妃：担心秦牧的安危，劝他离开无忧乡。\\n    *   开皇：两人在天外进行真正的决战，表现出亦敌亦友的关系。\\n    *   叔钧：再次作为见证者，见证秦牧和开皇的决战。\\n*   **其他重要细节：**\\n\\n    *   秦牧对剑二十式的渴望，暗示了他不断追求进步的精神。\\n*   **章节总结：** 秦牧离开无忧乡，和开皇在天外进行真正的决战。这场战斗让两人都对彼此的实力有了更清晰的认知，也为未来的合作埋下了伏笔。\\n\\n### 第995章 坏弟弟羞羞\\n\\n*   **男主角的经历：**\\n\\n    *   秦牧前往造物主一族的领地，和秦凤青见面。\\n    *   和阆涴神王商议接下来的行动。\\n*   **男主角的成长：**\\n\\n    *   在制定规则和管理方面，依然有些稚嫩。\\n*   **世界观与设定：**\\n\\n    *   彼岸幽都的设定：是与彼岸世界重叠的，未来还将延伸到太虚之地。\\n*   **人物关系：**\\n\\n    *   秦凤青：依然保持着天真和贪吃的性格，但也在努力守护自己的领地和族人。\\n    *   阆涴神王：对造物主一族有着强烈的责任感，但同时也渴望走出彼岸世界。\\n    *   叔钧：继续跟随秦牧，并对他的一些行为提出质疑。\\n*   **其他重要细节：**\\n\\n    *   秦牧和秦凤青的互动，展现了兄弟之间的亲情。\\n*   **章节总结：** 秦牧来到造物主一族的领地，和秦凤青、阆涴神王见面，商议接下来的行动。\\n\\n### 第996章 再入天帝后宫\\n\\n*   **男主角的经历：**\\n\\n    *   秦牧与阆涴神王和叔钧再次通过三间房前往天庭。\\n    *   遇到火天尊和虚天尊并成功脱身。\\n    *   误入天帝后宫并遇到嫱天妃。\\n*   **男主角的成长：**\\n\\n    *   对天庭的局势有了更深入的了解，意识到其中隐藏着许多秘密。\\n*   **人物关系：**\\n\\n    *   阆涴神王：秦牧对她又渴望又警惕，处于一种矛盾的心态。\\n    *   叔钧：开始展现出一些特殊的感知能力，对天庭的秘密有所了解。\\n    *   火天尊和虚天尊：两大天尊被困轮回世界，侧面印证了造物主手段的强大。\\n    *   嫱天妃：身份成谜，很有可能是天帝或者其他重要人物。\\n*   **其他重要细节：**\\n\\n    *   三间房的危险性：即便是天尊也难以轻易脱困。\\n*   **章节总结：** 秦牧再次进入天庭，遭遇了诸多危机，对天庭的局势有了更深入的了解，也接触到了更多的人物和势力。\\n\\n### 第997章 牧天尊府\\n\\n*   **男主角的经历：**\\n\\n    *   秦牧离开后宫，在南帝朱雀的行宫附近，被告知自己被天帝赐予府邸。\\n    *   回到牧天尊府，与狐灵儿等人会合。\\n*   **人物关系：**\\n\\n    *   狐灵儿：在天庭负责打理秦牧的产业，对他非常忠诚。\\n    *   龙麒麟：一如既往地喜欢美食，并对秦牧的感情生活表现出极大的关注。\\n*   **其他重要细节：**\\n\\n    *   天帝赐予府邸的行为，暗示了天庭内部对秦牧的重视和拉拢。\\n*   **章节总结：** 秦牧回到天庭，拥有了自己的府邸，并和狐灵儿等人会合。\\n\\n### 第998章 屋漏偏逢连夜雨\\n\\n*   **男主角的经历：**\\n\\n    *   秦牧试图阻止云初袖和阆涴神王见面，但最终失败。\\n    *   云初袖和怜花魂同时前来，场面变得十分混乱。\\n*   **人物关系：**\\n\\n    *   云初袖：对秦牧的感情复杂，既有利用，又有真情。\\n    *   怜花魂：代表帝后，前来探查秦牧的底细。\\n    *   阆涴神王：在天庭的行动受到限制，必须小心谨慎。\\n*   **其他重要细节：**\\n\\n    *   多方势力的涌入，预示着秦牧在天庭的处境将更加复杂和危险。\\n*   **章节总结：** 秦牧的平静生活被打破，各方势力纷纷涌入，使他的处境变得更加复杂。\\n\\n### 第999章 入宫面圣是非多\\n\\n*   **男主角的经历：**\\n\\n    *   秦牧被迫入宫面圣。\\n    *   在养荣殿受到天帝的召见。\\n*   **男主角的成长：**\\n\\n    *   对天帝和天庭的秘密有了更深入的了解。\\n*   **人物关系：**\\n\\n    *   天帝：身份成谜，很有可能是被多位天尊操控的傀儡。\\n*   **其他重要细节：**\\n\\n    *   多位天尊共用天帝肉身的设定，揭示了天庭内部的混乱和权力斗争。\\n*   **章节总结：** 秦牧入宫面圣，对天庭的局势有了更深入的了解，同时也面临着更大的危险。\\n\\n### 第1000章 东宫太子\\n\\n*   **男主角的经历：**\\n\\n    *   秦牧摆脱天帝的召见，进入天帝的肉身之中，试图控制这具身体。\\n    *   遇到东宫太子，险些暴露身份。\\n*   **人物关系：**\\n\\n    *   东宫太子：上皇时代的人物，对天帝忠心耿耿，同时对十天尊有所不满。\\n*   **其他重要细节：**\\n\\n    *   秦牧入驻天帝肉身，暗示了他在天庭的行动将更加大胆和冒险。\\n*   **章节总结：** 秦牧控制天帝肉身，试图在天庭中获得更大的权力。\\n\\n### 整体分析：\\n\\n*   **关联性：** 这几章环环相扣，从秦牧解决无忧乡问题开始，逐步将他引入天庭的权力中心。解决无忧乡问题，是为了让秦牧摆脱束缚，能够更加自由地行动。进入天庭，是为了让他能够接触到更深层次的秘密，并为未来的行动做准备。\\n*   **伏笔和线索：**\\n    *   秦牧对剑二十式的渴望，预示着他将不断提升自己的实力。\\n    *   天帝的真实身份，以及天庭内部的权力斗争，将是未来剧情的重要线索。\\n    *   东宫太子的出现，为秦牧的行动增添了更多的不确定因素。\\n\\n总而言之，第991章-1000章是《牧神记》的一个重要转折点，秦牧的行动从解决单个问题，转向更宏大的目标，他在天庭的命运也充满了未知和挑战。\\n";
        
        // 执行验证
        boolean result = validator.validateResult(markdownResult, 10);
        
        // 断言结果
        assertFalse(result, "章节数量不正确时应返回false");
    }
    
    @Test
    public void testValidateResult_WithEmptyMarkdown() {
        // 准备测试数据
        String markdownResult = "";
        
        // 执行验证
        boolean result = validator.validateResult(markdownResult, 2);
        
        // 断言结果
        assertFalse(result, "Markdown为空时应返回false");
    }
    
    @Test
    public void testValidateResult_WithNullMarkdown() {
        // 执行验证
        boolean result = validator.validateResult(null, 2);
        
        // 断言结果
        assertFalse(result, "Markdown为null时应返回false");
    }
    
    @Test
    public void testValidateResult_WithNoChapters() {
        // 准备测试数据
        String markdownResult = """
                这是一段没有章节标题的Markdown文本。
                它不包含任何章节标记，因此章节数量应该为0。
                """;
        
        // 执行验证
        boolean result = validator.validateResult(markdownResult, 0);
        
        // 断言结果
        assertTrue(result, "没有章节时，预期章节数为0应返回true");
    }
}
