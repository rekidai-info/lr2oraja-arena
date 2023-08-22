package bms.player.beatoraja.arena;

import bms.player.beatoraja.ClearType;
import bms.player.beatoraja.PlayDataAccessor;
import bms.player.beatoraja.ScoreData;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ArenaConfig {
    public static final ArenaConfig INSTANCE = new ArenaConfig();

    private final String httpServerUrl;
    private final String mqServerUrl;
    private final String playerName;
    private final String playerID;
    private final String arenaClass;
    private String skillClass;
    private final int arenaClassNumber;

    private String calcSkillClassSP(final PlayDataAccessor playdata) {
        final Map<String, String> classes = new LinkedHashMap<String, String>();

        classes.put("3e5af51ef26b6797b33d340ebcd41593ab5a7de6d14f0a3bb2225f70271fe07b66d1d6bb318bdcedf6444ded8818eb3fdd27edc09d9a2ba4a6ee2a10aaa9343c917fbd64f68d06bfb1f5dd41384ad919ef93aed74d78d94cc5458f6ece678555486bab83d726b9b3dd05319278ba6c4ffdf2467f9c89afc590e738ab063304dc", "(^^)");
        classes.put("72f4bdb5d87574955b896508d63f3df0b4a8684dc1731a3596bb99532bccd006e5d0e50edb8d0dc6df1a7a45c398090c7ee0bb3697d397f54d0208eba966a7a230b4cae3977b4fa4aef3e2fb3d5ee271ed906cab71e4906008e0271b8a254057e64d9de0b8a09f6e760c30917691f659b224fe2e7e1a9e6b920c58c2a6b35db4", "★★");
        classes.put("15e7de6e73d972d867ab11357c2e786d5d7e425cd042d087ab7469c8a7b137e6070a72804c27bc8a2845dfba3d1721e342651ce95bd76a5a9cdff509c83f478270d191d70a1f963f6b5e3b4cf9a45c67a3d05ac31c7d7678834c615e85fa19d449a9969f1f06a1af0a944f2f4a948a11df9a55cd2411a93fa35a4a2fcc6a9ba5", "★10");
        classes.put("54a31a861d0f33cd9bc3a35e439d1a5500b37754e3dc001dac7f080a5e3b678634f730b67dba89d282576ddd6f5b9b75069411be59408d841fcad42c912eb7af984c70e0ecf952e997b7466b238b45c475125fd9b0b8067db65f2b0b407e47f1b9e21b7e3e5e339eac6eafe9df77546b0b3849459f74418a0e57698286fb83ea", "★09");
        classes.put("16d3388c6454fb82a5ca3cf3989632fc93f8597350334b5878db12a7594a91161093cff05f633293750047f86e1b7c27ec4b72b84aeb9509866262ae615d0bee2442773f59fafc33d1d06d33492605eb15d0ed3c949e22b555d5bfc05645eb1bc676804a80dff450193c48d712f0e572a22411fecec30a6493d3640e6d8722ad", "★08");
        classes.put("54e031e4a94cd97449e288817e15fc45296f415e1b5f4391dcd06a74d1555d4308df23052729bd8234a0422db4b370984b970ad54b7b2474448e1de43972f2a39cbe3e546d41c2f69ea0d149bf9ca4a0a7794654e7db5e58485004593eb16a0fb96e6ca0dbf2e528ef4433ddbf9c4af035176d8041d6a14adc38a1783c020cc0", "★07");
        classes.put("1a50346964e7433f0192999c282128196f8ecc25208cd77116053e62f056a459d0cf7eb24c43c90661590f33b061abb8e1e2a9e062c101c51be04ac118aae20715342d1ce0c38f4f63bdfcd938b1bf7bbae4758dfbb13693feffa72247e8a91437b5d49ab3e9155039e5fe8a3438f15eef1367fa98b6e1c1148359aa83b13343", "★06");
        classes.put("60a7477fc8530c34aacb0a9b31fdc3f9f085d1c8ef29490f1f6e48da176855ffda881cadb23e12eb165c15cf826245fff8346f5acd606e791486f0e15342f0c2e2ed2ea0d91d9547dea6e15e6aaec9b9025a94e6b3cd06cc1378fe943da8042885bd616c7c739d7a1e0a26cfdd67b2d702475e4998a50914db7ff38ebb0db067", "★05");
        classes.put("30085b2c55604e42a499d147f52e63c29c6b62760bdc15c9bc3b46fc00ed95270d24750f644355d801fdadea903a1d6a56e29259f26f6dc8ed17aa993fa4a8f9daf400ce7c2a71b27ef11ee32268b548bd6454def3b80407b23a69e11d6d3058de23f24656d8846f5f51956e42e2d92852df8f950a08e97aa9a8f234a0a89d28", "★04");
        classes.put("35ee1486f03cb518263527d854984e66b3335345eadddd5fc53c44847fad68461c5f53f9ef9813d58c488bc7780a7f41dcf05c5595e4e639b7d0ae37961fc8d13adef599680b5d867adb4bf3308ce9770aed91fcd609eb14a0892fec0415e97325d9d442e226ce6b8fd7c061c7427371cbd2e7300109f2cd5687cfbf5dd61146", "★03");
        classes.put("179d2f8c2e765a88f830ad540f737af210cbb9f4eb32885f72fdd25139140ae1db0451a842b8a31f079e7d8b7da03e26c9c3c92ecebd695d0d9ab81139694e3122ce6af86ec76a0cb84e824f23420683a417268cf0a642255684c4c0241d456457793187e5f54781f7efebb6f4ec4bc6fe6f2cbbd2fe2256638443d5a157a023", "★02");
        classes.put("b7a19f718d9d6a03980ba977b201809bfa89f16207e6dd9e58609b9c4e399ad28f44566ba6ea88dc64c49c5ce4646e4ae6dbe0e438b25fe054a9b7ab2912ba66c0eadb148bf64d6718155220ff77028ceac669fc367f1f7dd32d3441ce04eefd6bc985dc9a28a7654a84744e1f20062e2cfc2aae5026eb465b246f64826f1592", "★01");

        classes.put("b79af9b79e4348b2063c2b5cbde2cd965fa54d3d5d7a4492ce386180d85db7f8a399dc8e429694da0ae9caa17aa74e87eedbfc5fafd14a830f30e6b7897b7ddee82afff32da8b2a991f9c7ef268c2ffc831652a3d1a1bb2a79800cc4dfb07e6926a3ed94feae0b14cf6d4ac0451fca675670e3590f2029bcd3f11fbc49d42b64", "☆10");
        classes.put("957c3f0eb8fabb463485f105756056819d31065b83408f1e3d47b4b7ea06bc98206b7c1b30d4c12805f0c4cb2ce8ce0ece18b40ef2531221e0faa09cf660c3d1f9cdf871f6f994ab0678e6d6a07d9e8cea2b4e726601a6ac94d8824619901259ef46c028b64d469c7d19172e955cb58ca2c7e6572c71baecdb12cca4289ccd68", "☆09");
        classes.put("8898a2d68a3bd897a4fa960f68d617c956a4cb3c6ef0e11fb1fddf66035da083022bfd54f4911cb8c2e34f2331b1ce448a1ab79f29ca30f7ae45572649439e3e2b4cd1c47592774afa6f0bb838288fb4263759e1ecb1c1b93cd69ab75631614888f7eefa7bbcf2634249ddde2b3bc39f8d97deca7fa09a8a9a78d92f8a06ae08", "☆08");
        classes.put("2945e8009fe18d91f8bd29b585f5896558c7109d8e76bcb314f12c6fdfdd24007ee5d15d045f1a94c1e93c6d39b7854028a932419a8d4915d9b11f742c93ea1e8863bc8c2aa990cb0255c284d816086a34f16a3c24f743ee74415e8e13f6271c224534597645319a5b5a8bff39f046e1858e2b632b79db148aa78246aa7da321", "☆07");
        classes.put("ce1fbc0148c4dda8df71e9eaa73285bf58666d11078f1decf1b40654586cb5cfe7d74b59bc583fce48ee4acef2aa8b7cc8f27b6038e9dcc31827ad6262b07a8ed0fa6cdf0fdadf0ab5b767a9e24c529ef7031dbeec216c0eeee07da2029850b543e3300d0d300b0863e6a7698d348b959f34308f6a038a1eaa3a659fdfa73bc6", "☆06");
        classes.put("4b245271ec7ff7c7ca611ff88e7199c6a101e0cff8b61881ea0c856720c77eac56b716744761053db39ae82ace6f98dff7221720c2705078f14028db311e1f088e78aecfab2d887902560e0dd4fd1ed6b7a7932b49729e9664741982bbbb701537c34d39ebf4345b6d623bb6f3b990264509fd20440c50a818669a4a53c5ccf7", "☆05");
        classes.put("5b7cbf452689aa56410c753cd48390df1dceaf723944d0023e352eaea3a2bf56766c2c1e85c74b5d1aaa921f316afc5213c32568f427a638c8f97a58823852727e523e6108a2a84070fa4d636d880e5ae295c3be671b6707fb6dde62ea1456b2fd8cd3057e8660f975246411e92553c7488ae0eed84155fec553c55c2000a40d", "☆04");
        classes.put("9ffe1b8da9077fa051d4f1ad6b16e7c3e383694d7db15188e4064bf6970c17ff01bb2c899ea3e930a1a3c1714fe42989088506aca202240bf654c24254aae4b1cab023d0e3592ffdfc966c4b364edc2e8657db77d73931b294874e6fad86e37254f1d613fc3574b9dd529d619c27864d46fb0702379141cd42d1a9873ca32505", "☆03");
        classes.put("c410897043fe8b6ede4a102f60e6e25c24b17bbc3314075c0f93bcf785dc103e43064e21dc75cfaa5c4de6df51b81fd8484dfb88346029f907e926da3a8beb4c3f2a9d8e7b32868b48bbfccff2f41e51918a750c516e62df9ff83ff1cd7e6f656d3eeb1725f0a5c64b742852248b1c4a11030eb57b796db4b18d5200466277af", "☆02");
        classes.put("1dfdaa94e9ece965e2ea787a8383399c43fc0dd3ed65f0c28b20be9ddffc80391703d92f5adce5dca1a1eab9198eda17bfc37be0691fd5b30ac3c2f54020404c92f8f2d5d40604d1b387c269ec4a4c6b48c211ac66e058eb7add053ad6ead3c049647e5c15d6043e95179298fc3b853a9db1b4733d67f606fac31335a4463322", "☆01");

        for (Map.Entry<String, String> c : classes.entrySet()) {
            final List<ScoreData> scores = playdata.readScoreDatas("sha256='" + c.getKey() + "'");

            if (scores == null || scores.isEmpty()) {
                continue;
            }

            for (final ScoreData score : scores) {
                if (score == null) {
                    continue;
                }
                if (score.getClear() >= ClearType.Normal.id && score.getClearcount() >= 1) {
                    return c.getValue();
                }
            }
        }

        return "-";
    }

    private String calcSkillClassDP(final PlayDataAccessor playdata) {
        final Map<String, String> classes = new LinkedHashMap<String, String>();

        classes.put("803a1e3d0201319a447fc71dfc955230c0272edbc7b69d1abcaddebea73de8a7dfe31bce3e6d63b755e333e59a4421a5cacb3cd97c3c5427f8f5c49a8d165ba5cf6a7762ccbb67d9421ec0c1ace99ef85561a7b1a87f1a703636104a5418cfd3c94f2b32d34f5e575eb71cb2256af0abc260a6b0fac3daf6eeaa1420f203f406", "(^^)");
        classes.put("e44e1107b08c32a7c0c9954f3dd357422584100d63bb5450fe5eb49c8111bed31cb37bc3a6f906b78044b84afd8b6248b11379d437f333edb2ac487102c69bc3633e07dcc02261adac1d4ae05ccd64ce5186b27fab63f2bbb4a6b0cebed46ed627fcf565516cd528f3c11ad53f5bc30d1f717c8d0a935944f0d3ccf310dadc2c", "★★");
        classes.put("36ceda97dd33c282118e3e6d2161a67d8c3e67109b77220db75deaa79410fc84c04c8b1a7617e5fcca89f4cb3e1f915e6dca23ffc6c5bd275345c4bf88f7217cf2a3804f5bba0e7c630e92ccd6161d606fd9f3f8aeb18b1ef12c43a2c7d2f68fa958994dd611e6b895fdd0bd1fe0908940de734814d565c6e576f71e39e46673", "★10");
        classes.put("5fc9f07cb64cc7b7ce9b1047c4cfbd2eca4fe62d61b8da49c56868b8a4e96ad224adec9397ef63c032b0d790635885e5e688cea4d4387e2106463c1a42b43ebdbf0e10029d0db36661e9163d24abc6a35d03b83e5337b0bd34b2052400f519b27c8db1e669af39c5dd379b4f63a56cdda38d86b2333d7e1aa164097684fe7d96", "★09");
        classes.put("d473f742cf15f35844d4ce28acc026f1a757cccdf77c831d458972b8411c00d49b22adf0281648d05e02417523f28633297752ff90cfb5b382352ab55d237f4f38ad08e7e2fc54c0925876cc5ac2208fb9d5bef34f38e0a9ac2c3a3400ed18c93477ca3a88b5dc77c3d855c9a78d53253550d4c13955cfcd972b5604ba526163", "★08");
        classes.put("1009fa438f6fb4008dfb67fd6ff41ed4820851d54eb072cc28320ef809f6d13cf4d7b32c8be69e5ceeeb452f13d09e6b6ab0e81de8c10e13b6e9b2df047ad191035998c54f5162dde4dc9fc5f9505a8338aa3dd406bda510ad19881153fff83ca9db0bd2f2d94d8f14d47b3d0d5ccab4086db78b095f7992d870014b326f0812", "★07");
        classes.put("9b7587ac719624a53bc7a88d73acc158182fa52005c8ff8b63b7ceb3edaf37abe5ef165d88f2113eca9050147e33b4fb55b59cb54aa220b51455a52ba1e5db9029437893ea96ade5def70f5474bc5b2e1173b1c544898eaa17c11dd71e40cbdb3225f879c387530ebf39cab1e2ea76eb0d32b857acd115fe36cbdbb9023429dd", "★06");
        classes.put("1b4bf6670c1be3e00717c0aa11a37e6a99f0d25798566c5888195f2400fac3131e73386112df97861fa9b04a1f4d15ff388f12964bb6745e79381f195e87ea5337ca0fb5422871ada057bbd4782efa96c76f8d0bbc14efe8e108e263cf130eabf40e64d2c299d0e16bb3c600bc430a900caa8f9a0bd232b139bd52b6b83a3c52", "★05");
        classes.put("79ef1632f2f9ffd6d4c991a94792ddec2299fd6088546abdff41567a08e1ffd4d19b439ffd0719679357c46bbd7d8656b092855fbc3a4cf73bdf84bb0f4b1e79933e6ae0a87335c9b8a014bbbddb3978e31d43544fe5c8bf4cc273bbfaeb150a12ed1940a1be7937b4954804d5d3000bd329481952ed5fca1fab77c05dcf42dc", "★04");
        classes.put("9e0e9df4120f1bf16d366fe1f185e930d81988da76cd1a448da469ab920b003fd6d53d1a88a402ec817c1eba9a7ec4053c691a46590449039e3962209019aaede8507ac34aadcf013adf0fa5a8aae3c904a278416ee34f5631c9d9c75c1a28dc665d3e0229369b7878e63aea039dbadf31d0931d119e6ef861d5c4c735f51a59", "★03");
        classes.put("6672c50ff7bcf39cd228f1fc6640c44165e4ecaedb4205aa36a6730f6519f5d50bf6ab004c7f0d2a395a2d6c254da50ecf7907388ed4e5e5531539c7fbd59ead2bb5e1c9d597e139d892b0fb34bed4136249970bbc350e58b4e8b0db5c4c97bfc40b612227d4dcd8bdb864d4e946fda59fd0b783824576551a775596778b38e4", "★02");
        classes.put("171dabc5b722806ecb589a0f7706725a73266b794010b77c63b780f92f11790d8d4d2ba3c749283f17bc599c0c960fb54fac87e37b38a9b32d2faa7a05641e673a2f62669c8058c5088d7e86f03bfa20233eb5070f8a52bf7652c0bc5c4fffcd3bdbb87ce19440f7c5b06cfeb422d9f34bf6cfcf657a259876dfeb3377d566fc", "★01");

        classes.put("7a40f48c9845e256eb1a112e280c178bf9fbe05f4f4558850547a670eb1f3d62cf9fc72b58e602da672a730efe47882b8043e7fde65ae3568d44648f55966afe60e003dd1408778060f0c63c7a4c16ed229dc9b4e0ade3bbcedd6b81f92dc1abcae895cb315c898bc471756821a94a85b418e5af50c7ea7eb07d836e0824e61c", "☆10");
        classes.put("99cf6211a16fdcf7f8d745a1969d717f8b508384541d183e24db7bde57fa638d5045368acc0c91d3d7ee1c9a03ed2f7d7f3849e49c6a6e628d858f94629c6afcd644e34a05b3b3feab949e8bc483725c301cabf58185d6cfcdf43e2fc716a52fc98d444edfa634a183d8b98756707b966d38000c3b05fc3f3922d89c429bc94f", "☆09");
        classes.put("34491540a0c12ab7f1706fa6d3c86d9d38b3a4ef201efa3cefc5f62c228c6a3e7c0efbb8f6a2406a430586ca3f9d1547faf632db23d9bb017406d3b741c758badbf18ea3041676e74742df0577df778c079533dbd86ce1158fbb21bf5faee0086b5b0db350082b890918d1011ff3372050d7405b441e4f83cdfe87cb1d31c9d5", "☆08");
        classes.put("bd3449545738e668659a785aa7115fbae5c78a37c3fcac89e22f3a8c9dd6edc68b244c50b0f401a18aa8d955f5669e6976e95606d130483becb0395c23a3a4fefa3c6ef0e0f97f946075ccf5295db45213418d076bf09ef40894c36b3223253a0d1e0dfddb026e0b7c41d963cc0fe249d30bc8782612ec1187819ff2ea988472", "☆07");
        classes.put("22b92a8f28dc11507922c873ddfc448541456cbd0993cb821d7a54bc62b5bcda25f39eaa1446dc8cfa1bad701d9d61f1374fb812172114b85d1fd2358cbb8a80b864a7d7c41ffe0089c02be5e2d42f09d8f301983b4d7ebffc67824f6af29a005cc87bd977204cabd24496c6dad5d5501b5d4db54f47473eb85ec45635199922", "☆06");
        classes.put("75397d7b13ce445901c319574b6e1d68ee9a498da86f62f045d4e7f449e864f14694f2246a4828826a069797f017841fc75c0aac4c2c5ea04513f13b59b6e7990f9ca46a040f64ca03962af280983fe24196a7070dcacb73a07df37063dff974bf3a9a823382ae1aab07f6b1ea91be0a8eb1e324cb5b7e98aa8a876d6d736a64", "☆05");
        classes.put("24d010e77cebc4dcb1cb1b8ae91214ce51856e4e88a3f0d1799e6dc23c7939cde9dec09c8b9955cdc14de6f678cb6ee212001ec99b8df9618c47129343c91a72d72170563cb70d2b6258e709a618f995baa541acad6fef1ba4c744c1814aae328702d32522c5a1c4c86084c92864cd1946aef308102ad9989c962b50c79fcd5a", "☆04");
        classes.put("a3176edc061decc3bc9fde0c2862c1d4b7ff32af6d086bd7a8906f18b7da924a74b502274763347ed9cb2e2181657d666d9b8f0f3f6a2036988e94fd9991acb173ded944b08407b65837c0da0ec7665149108a270dc4263a654f0eaaf9b256d89dc1dfbecb65234775ec68209ebe69c604ec60d1962f41d99b7cca7654500569", "☆03");
        classes.put("ca10c790276baa3a6d6b0a27b891f795f9ae9e6fa28427f828d1df31fc5b5c69122fa9f20ecf1bfc7b1cbf53ade0cf2cf9aaa142b77f5ac24af387fff72862511163ef41a82c8c4e0db3caf6412252c2420a13af0e67fdf7bcc1abe6ec2603de453577ee8d3f0f6c94162c75f6e087d6062280ec20049d572b2839078b04e34c", "☆02");
        classes.put("e6c65d09e9e2caf078efd7a1471886a1c916350dde05606a82e00448e988a09d0cdfa25b2bf7fd64356d6b70de3b52f060bb90b1300e351b06f6400540627f3ba59b1c6f18eae2b84ac0500f28286b215d2426cf0faf2f8a5edcebf85f012dc8e689990723ff642434a27acbee3858534f9bc3fef34243581c54b29aa2a6d1c1", "☆01");

        for (Map.Entry<String, String> c : classes.entrySet()) {
            final List<ScoreData> scores = playdata.readScoreDatas("sha256='" + c.getKey() + "'");

            if (scores == null || scores.isEmpty()) {
                continue;
            }

            for (final ScoreData score : scores) {
                if (score == null) {
                    continue;
                }
                if (score.getClear() >= ClearType.Normal.id && score.getClearcount() >= 1) {
                    return c.getValue();
                }
            }
        }

        return "-";
    }
    
    public void calcSkillClass(final PlayDataAccessor playdata) {
        skillClass = calcSkillClassSP(playdata) + '/' + calcSkillClassDP(playdata);
    }

    private ArenaConfig() {
        final Properties properties = new Properties();

        try (final Reader reader = Files.newBufferedReader(Paths.get("arena.properties"), StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }

        httpServerUrl = String.class.cast(properties.get("HTTP_SERVER_URL"));
        if (httpServerUrl == null || httpServerUrl.isBlank()) {
            throw new RuntimeException("Set HTTP_SERVER_URL");
        }
        mqServerUrl = String.class.cast(properties.get("MQ_SERVER_URL"));
        if (mqServerUrl == null || mqServerUrl.isBlank()) {
            throw new RuntimeException("Set MQ_SERVER_URL");
        }
        playerName = String.class.cast(properties.get("PLAYER_NAME"));
        if (playerName == null || playerName.isBlank()) {
            throw new RuntimeException("Set PLAYER_NAME");
        }
        playerID = UUID.randomUUID().toString().replaceAll("-", "");
        arenaClass = String.class.cast(properties.get("IIDX_ARENA_CLASS"));
        if (arenaClass == null || arenaClass.isBlank()) {
            throw new RuntimeException("Set IIDX_ARENA_CLASS");
        }

        switch (arenaClass == null ? "" : arenaClass) {
            case "S1":
                arenaClassNumber = 25;
                break;
            case "S2":
                arenaClassNumber = 24;
                break;
            case "S3":
                arenaClassNumber = 23;
                break;
            case "S4":
                arenaClassNumber = 22;
                break;
            case "S5":
                arenaClassNumber = 21;
                break;
            case "A1":
                arenaClassNumber = 20;
                break;
            case "A2":
                arenaClassNumber = 19;
                break;
            case "A3":
                arenaClassNumber = 18;
                break;
            case "A4":
                arenaClassNumber = 17;
                break;
            case "A5":
                arenaClassNumber = 16;
                break;
            case "B1":
                arenaClassNumber = 15;
                break;
            case "B2":
                arenaClassNumber = 14;
                break;
            case "B3":
                arenaClassNumber = 13;
                break;
            case "B4":
                arenaClassNumber = 12;
                break;
            case "B5":
                arenaClassNumber = 11;
                break;
            case "C1":
                arenaClassNumber = 10;
                break;
            case "C2":
                arenaClassNumber = 9;
                break;
            case "C3":
                arenaClassNumber = 8;
                break;
            case "C4":
                arenaClassNumber = 7;
                break;
            case "C5":
                arenaClassNumber = 6;
                break;
            case "D1":
                arenaClassNumber = 5;
                break;
            case "D2":
                arenaClassNumber = 4;
                break;
            case "D3":
                arenaClassNumber = 3;
                break;
            case "D4":
                arenaClassNumber = 2;
                break;
            case "D5":
                arenaClassNumber = 1;
                break;
            default:
                int n = 100;
                for (int i = 0; i < arenaClass.length(); ++i) {
                    n += ((int) arenaClass.charAt(i)) * (i + 1) * 10;
                }
                arenaClassNumber = n;
                break;
        }
    }

    public String getHttpServerUrl() {
        return httpServerUrl;
    }

    public String getMQServerUrl() {
        return mqServerUrl;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getArenaClass() {
        return arenaClass;
    }

    public int getArenaClassNumber() {
        return arenaClassNumber;
    }

    public String getSkillClass() { return skillClass; }

    @Override
    public String toString() {
        return "ArenaConfig{" +
                "httpServerUrl='" + httpServerUrl + '\'' +
                ", mqServerUrl='" + mqServerUrl + '\'' +
                ", playerName='" + playerName + '\'' +
                ", playerID='" + playerID + '\'' +
                ", arenaClass='" + arenaClass + '\'' +
                ", skillClass='" + skillClass + '\'' +
                ", arenaClassNumber=" + arenaClassNumber +
                '}';
    }
}