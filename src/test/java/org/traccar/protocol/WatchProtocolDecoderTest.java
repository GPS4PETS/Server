package org.traccar.protocol;

import org.junit.jupiter.api.Test;
import org.traccar.ProtocolTest;
import org.traccar.database.MediaManager;
import org.traccar.model.Position;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WatchProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        var decoder = inject(new WatchProtocolDecoder(null));

        verifyAttribute(decoder, buffer(
                "[3G*9705141740*000B*oxygen,0,98]"),
                "bloodOxygen", 98);

        verifyPosition(decoder, buffer(
                "[3G*9705141740*00C2*UD_LTE,260723,185105,V,00.000000,,00.0000000,,0.00,0.0,0.0,0,100,67,0,0,00000000,2,0,605,1,10006,65799,14,10020,4104,4,3,,34:60:f9:ec:19:f8,-82,,98:48:27:55:18:20,-96,,34:e8:94:e4:06:18,-104,0.0]"));

        verifyPosition(decoder, buffer(
                "[SG*9059011020*0067*AL,240123,181628,V,54.427538,N,6.409275,W,0.00,0,0,0,19,90,0,0,00000000,1,1,234,10,55C0,3B882A2,132,,10]"));

        verifyPosition(decoder, buffer(
                "[SG*9059011020*006b*UD2,240123,162011,A,54.427621,N,6.409190,W,0.00,0,0,8,19,88,0,0,00000000,1,1,FFFF,FFFF,FFFE,3B882A2,132,,00]"));

        verifyAttribute(decoder, buffer(
                "[ZJ*5678901234*0001*0009*TEMP,36.5]"),
                Position.PREFIX_TEMP + 1, 36.5);

        verifyAttribute(decoder, buffer(
                "[ZJ*689466020014198*0003*0113*AL,221121,085515,V,00.000000,N,000.000000,E,0,0,0,0,0,44,0,0,00100000,1,255,460,0,16399,234887445,0,6,WIFI00,68:77:24:1b:e7:a7,-59,WIFI01,68:77:24:1b:e3:30,-75,WIFI02,68:77:24:1b:e3:27,-75,WIFI03,00:41:d2:c0:f2:f1,-76,WIFI04,00:41:d2:c0:f2:f0,-77,WIFI05,68:77:24:1b:e3:d8,-82]"),
                Position.KEY_ALARM, Position.ALARM_REMOVING);

        verifyNull(decoder, binary(
                "5b5a4a2a3738393436383035303034323639322a303033342a303433392a4a58544b2c302c77617463685f375f32303232303532363039333935342c312c362c2321414d520a0c0a3c3f96d98367e9468ea245320c0a3c3f96d98367e9468ea245320c0a3c3f96d98367e9468ea245320c389814ffcd762fe49d50ae7a2e0cb528aefbf76911df05c2fbe17d050c2200cff77ef0df4d9b4ab9a4340c449814dbe7c63fa82bc3750d800cc48abbffddb0df8e8fda95e5980c49982ff6cf65f9377d02a39c3aaa0c389805f2ff42c1b80e0a0eb1dc0c2998e9defe15cfa3bdbe80d3540c7298c2f6d9239e3eae3c4a81660c490034dbfd513fedad0c2fc3900cc40039b7f71bb0657ba75558c40cd7813b97ff7219777ec7f401260c7d040003bff6f75fdb898a6ba1140cecd127f7f83357cb73a68a5f680cb081d4f6e749e6af8ed367bc480cec1815dfffd2dbed358112af320ccc21179fffbd17a3a61c133b380c920047defc72a784770ec0fe400c383c42f6faebe76fa736e9d1be0c4918e7decddc67ec9afd87ff220cc418e6bffe6cf6c9ac1f83c3900ca6cad1ffe3da24e1be3b547d03c00c6b00b8fee77f60a76d3e7d0292e20c2918b7bfff76387b793d3a36300c7d0400b7fff7f63ae513ac6f74de0c980016fbff7d01f9b30fca67c7220caa982ff6dbd7bf3d8dfed143ec0c44982fdfcb7b517e26f6ea52420c0ed19cfedb438f179d3fc50ec40c008ad2ffff635fe28dc1ec1f860c76cb7d05bffda53eaebe4d201ae20ccccaffbbfd3db4abb6ddf39d0e0c6b00acfeff406fe4a12661caf80c76982fbffffeb17b2f65472f300c7d04c24bf6ef1b10e47f73fc10b40c76036cfecd4e7837145a6a8e900ccccaf2beeba36fbaa36feb60640c7d0400dfffff73bfa3b87d0256a1700c7d04e4efd6efc23b651eb73e77780ccc1813ffffb39f6baa6f3195080c00007afffee0b9df6346ca08d00c6bca7d04feff4d64a7b56f9855da0c769819fbfd954ea5bd7d040ba6420c4c4c09bfffd5f6c57d01930e7d01220cc48a22dffe30bfcbaf08a795140c7d040433b7ff3cba5f3e336a40060cecbd2bfaf775bea7bde7e095ee0cc4caaef7fb623feeaab69eabc20c8c0021bffd1ea4a1b090175a920c7d046445fbdfc1abe910b0ca56160c7d0440cbd7ef03893c7f7d02e1a8c20c85e42dff5fdaa145759d326b5a0ccc4084f75f8eeb7b15e4eb4ff80c6bc22ef7d7eaf8df3b8ff678cc0cca4026f3cf7518fd731ceca3560cec041ffbe7655eaf822f04c4fe0c7d0478e3fbfcb5dfc8a81fdbb9e20cc418e6d7ff777f26affe37cc020cd7977cbff7d0aecb9727be6a0c0c00bde1f2d797fc8754ed09d4ae0cc498279f7e729fcb9eff70c1a60c7d0478e5bfffc67eef953fda69aa0c7200f1bbf7e891ef5a287cb5b80c983629fee5555f739f8a29279a0c76d103bef73f55a6bf89ba8ce40cc46424f6ded9a194167a067d05880cc4780efeffc37fae944d89bfb40c4464bffffb6dd54e8344394a5c0c49781ffffc653feaa31af59ac00cc464cebfd76ae4e8a55d5b5a4a2a3738393436383035303034323639322a303033352a303434332a4a58544b2c302c77617463685f375f32303232303532363039333935342c322c362c5f24fbbc0c7d04983effcfe35fbd8fff7ce6f60c448a1dfbd715bec3b42448e58a0c0e0421bf5f17ebc31a43301bc40c768ababf4f66cf629ee31fe9900c6b6426bf5f4eb7669dacc439140c945733bff9351e7d04ab6be54ef60ccc98bfbffbf67f63b78d8f42880c7d0400c6f7fffab5cbae8e8275da0c0097ffdefff5dfafb0c4727d04980ccc78d1f6ff1a6b7e37631059fa0c760045f3fd853fea877d03aa87440cc40022f7ffa8b5c58f6e80c58e0ccc4c1f9ffff32fa7af87de04560cb09801f6fdd32efcbdad9495b60c6b987d05bff72d61eb687d05a9f9e20cca57c7ffff6cb1fe3387ea596e0c629823dedfbfc50b97965e44ea0c4918ccbfd7cc75e59fff92e9ee0c8c78e7faff707ffda60ec3ada60c30c217befffcb8f3290f06d75e0c0e8a7bb7dff3eec7b7928ef6680c38ca1cdeff272ba012597d051a520c0018dfffeff27d01e369b3bcd7d80c98146eb2fc7f45c38e1ce53e120c3d5700ff7fd44bee28aa089d900c007157bffffe30302d7d0583585c0ccc5747bfe7f73cff7d02de1098240c7d047865f6feca71d70bdfaab6a20cecb946bfd966be74b61a06c6660c441612ffe7eb966a8c2886cf760ccc7827bffd653ff7980a62e9320c00ca98f7fc1b311a3986700cc20c490017bfff70cfe7bb455637320c6228afbf7e6da4c59763b693740cccc2b5fee1951975760e4a9dca0c0000faffdb255f6f86af4be6fc0cccc2ffdfff7d0225efbc847c42760cb48a8fdffbe23ff1a78782cbe60c7d04980cffff6e6dfc75a6c65a500ce62808bfff24f853748a9537400cb4004ebfeb6e70aa4314903e8c0cb46408ff72d62f44945f64897d040cccca7abfff2db5c6bfcc345e700c7d04c24dd6fd5f95638f78974a800cc4caffffffb17d01c36bdd7d047c2e0c7d0436d1f7f8917f83af7fc5c2180c070449f6fe570f128577c8c97d040c7d047813f6df3fd17d026b6790d1b80cc400c9d6d92dc56497843ed10a0c44986effffe13fef8eef7c717a0c7d049872bbfdad3abe2be0d60b240c4900adbf7d02dfa4ef960fdf23580c629845fefc5a650fbf8b0d4cda0c7d040076b7ef273fe78fce983ae40c4a00ded7ff629f7fab4531501c0c6200b7ffedf13fe6a68f01fb6c0cc49809f77fdb91517fb39e8ba00c760009bf7fd6afe7b46921f27a0cecbd1df6faf99120776e807d03fe0c98ca23fbff3749fb5ed3909c160cc44cbbbfffbd70e77d0367bc2e740c499816f77fe2ffe08edd7d048c9e0c940021beff1df0f1338c2f1a3a0c7d040083feebba1c756e5760c2200c94981dfffdf921b92b99909ba40c7d04984ebbdf7ff0c77d025a1fbfc00c7657b3ff77f4befcae2bf625640c62ca53f7fdb35f2e92af3bd5c60c7d049878bbdfc0dfc1bdaa1918000cc404bbbfefb41ee6b50a39bf180c767874f7fb8ee156320ee600020c69981cffffe0fff6bb2764acaa0cec781bfbff7d0324eb9b4b4d5d5b5a4a2a3738393436383035303034323639322a303033362a303433302a4a58544b2c302c77617463685f375f32303232303532363039333935342c332c362c73580c07ca4fbbfff5bec5abcfac465c0c0042a8beff316aed38fe4603dc0c7d04577d04fefdc929f7294ce7520c0c76984dfaff402fe68ebe113b000c7d0436a6fed465ff4f8d8e3306300c44e434f7f79e84abb8fa081be80c983636bef7e128bf7d03d34c71c80cd7982dffd54cac94536beab1320c058bd6bfff701fe69d1a39d1e80c76caedb77ffea0e47febb469440c0098b2bef5e55eed981672b1c40cd800d1bef4bda58daf7d01d309180c08cadffbfc31799f5613fab2da0c7d049828beffa5eea0bd09a14c2e0c0000e59feb5f9dff7f4945e98e0cc40046ffcfe57fb58f7d053dd9e20c7d040047d7ffd38f67aa9db8d38c0c7698d2df5fe2efc6bb0f5a18220c980367bbfd95dae73e3bf2b59a0c49813796fdf5efab9d3ed7d3d80c9800ebd7fdb831ae8f91811e920c7d04ca74bbff5cdcf45605c25f140c013e029fbfe7ffc299376b409a0c7510009e7d02010f68824cccead60cb13b00f7d5821e6c8d594a06880c75ec00f6e782e8f06b0c610d1a0cc09843dfef117fa5b3aef236f40cdcc208ffff4831f83703ca20c00cc09800bf7fd71eec9ba34e3c200c650f6bffd5bc4dbe7ec5b0f1d80c0d1c1f92ef917fa0b96023eac00c8efb1db6bfa42eed9f69051d4e0c8eff23b2fe4486d56ef44f702e0c8ebc1b9657d46eeea852ac337e0c8efb1f96d9877fc9b34c38f1540c121c2796df13577c727f8cedec0ca9107d05afdd6a9d957ab76c02560c121c2dd6bfd33fa5b844f27d02340cb11037b6e3962e6eaf52d6e7460c2b1c3db2dfe61eef9aad3dcc0a0c542e42d763e709fe723a83ae7c0c7cfa56bf7fedb8763c3f8ab2c00ca40022efff2411604d22a485e00c228adcbfdfcd748293c66b0bf20cb10f0097ff806dcf7a0a640d6e0c5a0400b7e3030ef4ae0a6046f80cb82812fe7b04d77e4bab11894a0c541c24b2dd53fea5891e3824420ca9fb26b2d7156fa485049cac420ca91c2d9edb2487526c2e2260fa0ca9bc25b65ff28e41a34232c0f00c8efb25b649e51ec39c0ae6703a0c70f425a6c820dfaf2f8124c8960c80f42794b323aed9ca9b6924be0cb93b278e3e8d929c36bb70b6360c587b27524f85857d056d214841660cda2f278b4c876eb3186e5acc540ce6832f6e3f0d82f81226e6dc3a0c0b922feff50a03d03c98ae28360c03d62befb54ab611de0c8addee0c84d626b7fdb4461556dc0153040c0b0f27ff5781c5c2b32f6762140c49bf2dfaf521969b702ecb11d80c7d050f27cfed75bfab95dc4af3180c011c3d8e7451df80a5cd0948080c7c2e35f2c7b0becca22e50769e0c7c1c3d965e628f46a9a58ae6100c7c2e3b965f4a6b5ebd1436b0b00c7c863eaedf18c7816cdeeed7e20c2b3e3df6b73952b2b1abc048a00c7cfb3db6cd67ae68cda18af9d60c2bff3e96df12ff61b39627cd580c7c863fd6cd810ee595d85ee6645d5b5a4a2a3738393436383035303034323639322a303033372a303433372a4a58544b2c302c77617463685f375f32303232303532363039333935342c342c362c0c7cff3fb65f812fe196dc62d8360c7c2e3db2de738fae9ca7c7236c0c2b2e3d96df6c1d775a3df5a47e0c7c3e3f96fe943dc07e81e170700c75503fb7d6a6197f1b08df85440c5a133ab6d6a0e68f3acb04b7b00cfa293fd6cfc20c390d5265d3c80c581347b278077d03dc22d9e49c680ce64d47924e8dcdffe9be19dc100cb90f4f9e612d2201961c2e111c0c003b4febb58a96c809ab5a71040ce6d759fbddba54b2aa9d9d983e0cc029576ecf1f703099d84994520c4029579e7d0281577073f5629dd80c5a2952b6fed177177d01e57d0175c20cd8647d044f7e3c58aba523ddb0720cb56753d3e9c6799f95938401f60c756414ff5f00fb57bc4d7e111c0c7c085573ef743ee19ca30057e00c752e5ab77e34eec69f3cbe53940c542e4d96d6e7ff03854a9506620c7cff55b6efca197610a86606180c7cfb55b67eb25f4b999c002bf20c7c107d03beeb451fad8d042492740cb8f05eb5ff664f679c1cc991c60ce1671bffd33d9445b57d0584c7d60c07781dfef5e3ee81b45ff8a8c80cc40072b7dfe6fe6e978257253a0cb1131fe6783035868e439b00d80c583b27b17ecb53dd165c3422180c703b2fbeb18d023e14d90304700c3cce2b93cdc2e6b3550a5546160cb13b3b72fe24273859059b52040c224d34edc742f6bb7c877d02b5940cb84d37cdcfcbc3f03a041cf4ac0ca4923a9f3b74e805b235cce8660c759000fbfd90565072aa7d03bb520cb592017f411dcbe80a0cc420360c09d6528fcf2e25b382ee46d75a0ce2e853d3cfb617fd7d023f05e0780c07cc3bbabfa9f6cec5aa48ac600c404208fb678a4cf16a9c09098c0cf14244fbdfc2afe7932fe57d02900c011c369e7d0264a8570a54f9a2e80cb1103daed6249fe59dc888c2ac0cb1103573c5f697be759923ecc80c705f7d059effaaa4b09b976f375a0c804223bbed89767d041693edab180cb49819d7eb5a8573bb10c951540c3c643af74f021f25b6dd3b7d02040c07982bd73fb31fe38f09a227a40c037610ff7d03c6058098fef936a80c03b808debbe055a1a71319128c0c6b8f2e9e3bb52d71c226d3141a0cdab82d7fa9d416167a1cb950020c03c32bb23ee9e3743f3b6853060cc02937adb5157d05f4a6960c1b680c4076365e6b754bda9394a7aa720c06293ef23313fb1b94bcc2c09a0ce676006bbd8e0417941b90d23c0cb9294f7d01fd3d65748cf6d1cc220c03767d035e3a65e6f2514ab1db060ce6d965bf4035f65c79a9e4446a0ce62921dbf46457b56cd65f6c500c584d6bba7ea8166edd6cf7693a0cb939227bfca25f82a7c4202f2e0c06ce2b9a4f0d95f39eb43c22840c06901eef6fd15fe48f452000e00c62023afbffb269beba0ee0ac540ce6ec00f37b8139f2026e80f1440c22501dfe7d017d03ed8c3ff6803f240cc48ae3b7fd725e63a347cd64100c0094f8efdeb0a9f35c83eb06b00c005d5b5a4a2a3738393436383035303034323639322a303033382a303433332a4a58544b2c302c77617463685f375f32303232303532363039333935342c352c362c6647deef2df57fb68fd76b660c44e43cbfefc0beec867d025c3f920cb4507d01ff6f42ee6c1f58a0db0e0c7d045701ff7f19f9f90fdff037c20c4404259efcd47d02a52b7ef6df0e0c7d04d128f3578db14779993109560ce1e41ecffd2851af49256b1ecc0c7d049837ffff707168db3dd1ac240cc09419f7f39a7d013d6ec1b4e55c0cc4881cff5f49f55fadc52285a40c06ce9ad7dbb8349b97031ac3b00c407d049d9bed74dfadad950eab560c06799ffac7a924b18bdf11c3d40c8054a2f777522e65be921a77240c8042a19bff706fe5b7e3e7d0360cd82840bff3f4fe429bea0081560c0064419fd9157f8b9c797e92c20cc47fe2b7d5e6ee45bf0524010e0c0894299e7d037e4553bcbbc0989e0c40501bbbdc926f8daf53885e0a0c80971ffee559f5bf98ed54cf140c00581abbffac75b88b14d5e3780c5a046dd66fd7bfe5b7ca749df40c403672bbcfe47ee797860ded5e0cb5502efb7ee2ffc7bbdc27d0880c44ca08fbf7031ecd9321d21ef40cdc9806fefde36f0699ad1569420c22cb1bfbdc974fcb900ab601440c00cb1cfbdfd2f7d2730b0b46680c0b4203dfef309fe0adc121e2080c225829feeb901f25b926343bae0c624204dfff4875b69ceda4df4c0c499886ffe7301e8e8bf9ef0a260cdce408dbef829fe45337e98d860c010800ef76088d8015f3b2c84e0c755c3ecfc898f50fbcdb9344220c7c7d03459349337d01e4949da1b4ec0c7cf046ae6e4a90df3552070a5e0c7cf045d23fbba0951147986eca0c7536426e6ef2ef0b7d01c22fd2960cb8284e9db92d40552988334f200cb8024acdbfea90d13b545fe35e0cb8f04d5e48659d48b97c1096140c7c084f79df4402702126bf358a0cb8a74d4def66535e145638d12e0c543e4d8edfa2531b15a340eb800cb83e4eafbd9031291a8563e4780c54a74ef1c7300da79aa94bb67d050cb8f04eba6fb0325813871511b80ce20ff6fffd404ed58ecf264c8e0c7c0f1cb3fe1a36fb7187c379d00c7c3e52f6727d0261ca8bdd1361b80cb8d64d9a4fb1cfe0aadaaa7d05c40c2b2e44b2bfb9b4dcad9d7d0200280c7c3b47d5db803fc2aee71a77f00c7c1343b2bfa06ee6ad3228f10a0c7c3b4a76477fe7650b0c809ca80c54d74b92dbc327134514d6126e0c7c5f4a8f4f74efaaa5cd0a43bc0c7c104bb64ff5ce604fa2028b680c7cff4aaec7f7a9394cc4c599e60cb8ce52eeb553d215a6a466b4120c750f4fae5f9816c04a0a3f751e0c75e04f73c58e96bb4b925a4c680cb80f4b6e3fe1635614bbdeccbc0c7c3b4faf7e825e42d473ee76800ce298578ffcb61f0af936387bbe0cb800e39bf62410681583e0d2b20cb4987d05ff7f297bf69eaf4cd8b40cb57815bf7f300fcffe9fae966a0cb497acd75fd37f648d00d141a00cc48af9fffe1a3b979b8f8573e80c29bff1bf4b94c6fd7c3eec075e0c7cce1f5d5b5a4a2a3738393436383035303034323639322a303033392a303065352a4a58544b2c302c77617463685f375f32303232303532363039333935342c362c362c8f5cf9f34fa6edceb18c0ca4ce436e7d02d329de94b63eb11a0cb894438f44cf753dbc1a83b4560cb8e44391eff729d4155a231bf40cb59443f373b4288492cd6e38160c5a42049bfec8678ad42bce0c560cb12e39fec3d1fe24b5572ee0be0cb1ce35fe33688455aa74e3ffa40cb1d635df4b704160ae864a7d03440cb10f3267dbf4a21d540ca1051c0cb11337724f8747f5547d054e91660cb1a73692df964f3bbb0758d41a0cfe05b60c314460297eae4185f20cad673ceb6dfa05ddbe0278d5de5d"));

        verifyPosition(decoder, buffer(
                "[3G*9031853319*004E*UD2,220322,055105,A,22.761162,N,114.360192,E,0,0,47,14,100,64,0,0,00000008,0,0]"));

        verifyAttribute(decoder, buffer(
                "[3G*2104326058*000E*btemp2,1,35.29]"),
                Position.PREFIX_TEMP + 1, 35.29);

        verifyPosition(decoder, buffer(
                "[SG*9159059735*0066*UD2,230322,082138,A,59.55285,N,016.66185,E,0.0,000,26,14,80,70,0,50,00000000,1,1,240,7,34505,80806406,,00]"));

        verifyPosition(decoder, buffer(
                "[SG*9059056143*0053*UD,251021,223408,A,41.46500,N,081.53128,W,0.926,000,0,00,70,70,0,50,00000000,0,1,,,,00]"));

        verifyPosition(decoder, buffer(
                "[3G*2104326058*00E9*UD_LTE,300621,135101,A,32.162652,N,34.888748,E,30.84,265.158,65.621,18,100,83,0,0,00000000,1,1,425,01,10223,8012811,100,3,ES4104,22:74:1d:39:64:ff,-46,metropoline-wifi,a8:3f:a1:e0:66:ba,-89,Egged.co.il,00:0c:42:51:cf:cd,-81,1.7055488]"));

        verifyPosition(decoder, buffer(
                "[3G*358839237678820*0122*ALCUSTOMER1,251120,081821,V,0.0,N,0.0,E,2.58,317.462,35.147,14,100,2,11089,0,00100008,1,1,460,01,42308,101992452,100,5,shizhou1,44:56:e2:03:ea:2a,-69,FART3,30:0d:9e:bb:fa:4d,-70,ZKY-A209,88:c3:97:c1:f4:7f,-73,ChinaNet-HNeD,e8:84:c6:21:7c:dc,-77,,30:45:96:10:14:5d,-79,1.2035439]"));

        verifyPosition(decoder, buffer(
                "[3G*0304187088*0100*UD_WCDMA,100720,094202,V,0.0,N,0.0,E,22.0,0,-1,21,75,92,0,0,00000000,1,1,425,01,10192,1282125,75,5,Inet,04:f0:21:46:1f:57,-54,iNetSecurity,00:1e:42:25:2f:3e,-71,Gilad,58:d5:6e:9d:1b:af,-80,weekend,14:ae:db:cb:99:25,-82,advancemed1,04:f0:21:4c:c8:3e,-89,0.0]"));

        verifyPosition(decoder, buffer(
                "[3G*8809008845*00C0*AL,271219,094744,V,00.000000,N, 0.0000000,E,0.00,0.0,0.0,0,100,81,0,0,00010000,7,0,460,0,9336,3981,141,9336,3912,141,9336,3982,140,9765,4233,134,9765,4071,134,9765,4321,134,9336,4353,132,0,0.0]"));

        verifyPosition(decoder, buffer(
                "[3G*2104134718*00A1*UD_WCDMA,161019,134938,A,43.373367,N,71.157615,W,22.0,350.206,279.717,17,28,79,0,0,00000000,1,1,310,410,23999,132013696,28,1,Home2,60:45:cb:cb:34:68,-93,8.263865]"));

        verifyPosition(decoder, buffer(
                "[ZJ*014111001332708*0075*0064*AL,040418,052156,A,22.536207,N,113.938673,E,0,0,0,5,100,82,1000,50,00100000,1,255,460,0,9340,3663,35]"));

        verifyPosition(decoder, buffer(
                "[SG*352661090143150*006C*UD,150817,132115,V,28.435142,N,81.354333,W,2.2038,000,99,00,70,100,0,50,00000000,1,1,310,260,1091,30082,139,,00]"));

        verifyAttributes(decoder, buffer(
                "[3G*4700609403*0013*bphrt,120,79,73,,,,]"));

        verifyAttributes(decoder, buffer(
                "[ZJ*357653059860416*0007*000c*BLOOD,109,68]"));

        verifyPosition(decoder, buffer(
                "[3G*8308373902*0080*AL,230817,095346,A,47.083950,N,15.4821850,E,7.60,273.8,0.0,4,15,44,0,0,00200010,2,255,232,1,7605,42530,118,7605,58036,119,0,65.8]"));

        verifyPosition(decoder, buffer(
                "[SG*9051007430*006C*UD,150817,132115,V,28.435142,N,81.354333,W,2.2038,000,99,00,70,100,0,50,00000000,1,1,310,260,1091,30082,139,,00]"));

        verifyPosition(decoder, buffer(
                "[3G*6005412902*011F*WT,170517,133811,V,18.512200,N,73.7750283,E,0.00,0.0,0.0,0,92,82,4262,0,00000010,2,1,404,22,10125,8301,141,10125,13921,122,5,Skynet,28:c6:8e:be:87:c0,-60,Intel Wi-Fi,4c:60:de:32:3d:38,-70,Nirvanic-2,40:e3:d6:4a:d9:c2,-73,A4-Guest,40:e3:d6:4a:d9:c4,-73,A4Idatix,40:e3:d6:4a:d9:c3,-73,13.8]"));

        verifyPosition(decoder, buffer(
                "[3G*8308406279*00CC*UD3,170417,190930,V,54.739618,N,25.273213,E,0.0,323.53,175.1,6,51,83,0,0,00000000,1,1,246,01,200,13242758,51,3,TEO-189835,00:8c:54:58:1d:64,-84,Cgates_7137,78:54:2e:e3:71:37,-85,ASUS,9c:5c:8e:b8:d4:78,-93]"));

        verifyPosition(decoder, buffer(
                "[SG*9051004074*0058*AL,120117,145602,V,40.058413,N,76.336618,W,11.519,188,99,00,01,80,0,50,00000000,0,1,0,0,,10]"));

        verifyPosition(decoder, buffer(
                "[SG*9051000884*009B*UD,030117,161129,V,52.745450,N,0.369512,,0.1481,000,99,00,70,5,0,50,00000000,5,1,234,15,893,3611,135,893,3612,132,893,3993,131,893,30986,129,893,40088,126,,00]"));

        verifyPosition(decoder, buffer(
                "[3G*6430073509*00E7*UD2,241016,081622,V,09.951861,N,-84.1422119,W,0.00,0.0,0.0,0,39,94,0,0,00000000,1,0,712,3,2007,18961,123,4,Luz,00:23:6a:34:ee:76,-70,familia,b0:c5:54:b9:90:ef,-78,fam salas delgado,fc:b4:e6:5d:50:ea,-81,QWERTY,c8:3a:35:43:0f:e8,-93]"));

        verifyPosition(decoder, buffer(
                "[3G*6105117105*008D*UD2,210716,231601,V,-33.480366,N,-70.7630692,E,0.00,0.0,0.0,0,100,34,0,0,00000000,3,255,730,2,29731,54315,167,29731,54316,162,29731,54317,145]"),
                position("2016-07-21 23:16:01.000", false, -33.48037, -70.76307));

        verifyPosition(decoder, buffer(
                "[3G*4700222306*0077*UD,120316,140610,V,48.779045,N, 9.1574736,E,0.00,0.0,0.0,0,25,83,0,0,00000000,2,255,262,1,21041,9067,121,21041,5981,116]"));

        verifyPosition(decoder, buffer(
                "[3G*4700222306*011F*UD2,120316,140444,A,48.779045,N, 9.1574736,E,0.57,12.8,0.0,7,28,77,0,0,00000000,2,2,262,1,21041,9067,121,21041,5981,116,5,WG-Superlativ,34:31:c4:c8:a9:22,-67,EasyBox-28E858,18:83:bf:28:e8:f4,-70,MoMaXXg,be:05:43:b7:19:15,-72,MoMaXX2,bc:05:43:b7:19:15,-72,Gastzugang,18:83:bf:28:e8:f5,-72]"));

        verifyAttributes(decoder, buffer(
                "[SG*9081000548*0009*LK,0,100]"));

        verifyPosition(decoder, buffer(
                "[SG*9081000548*00A9*UD,110116,113639,V,16.479064,S,68.119072,,0.7593,000,99,00,80,80,0,50,00000000,5,1,736,2,10103,10732,153,10103,11061,152,10103,11012,152,10103,10151,150,10103,10731,143,,00]"));

        verifyPosition(decoder, buffer(
                "[3G*2256002206*0079*UD2,100116,153723,A,38.000000,N,-9.000000,W,0.44,299.3,0.0,7,100,86,0,0,00000008,2,0,268,3,3010,51042,146,3010,51043,132]"));

        verifyNull(decoder, buffer(
                "[3G*8800000015*0003*TKQ]"));

        verifyPosition(decoder, buffer(
                "[3G*4700186508*00B1*UD,301015,084840,V,45.853100,N,14.6224899,E,0.00,0.0,0.0,0,84,61,0,11,00000008,7,255,293,70,60,6453,139,60,6432,139,60,6431,132,60,6457,127,60,16353,126,60,6451,121,60,16352,118]"));

        verifyNull(decoder, buffer(
                "[SG*8800000015*0002*LK]"));

        verifyAttributes(decoder, buffer(
                "[3G*4700186508*000B*LK,0,10,100]"));

        verifyPosition(decoder, buffer(
                "[SG*8800000015*0087*UD,220414,134652,A,22.571707,N,113.8613968,E,0.1,0.0,100,7,60,90,1000,50,0000,4,1,460,0,9360,4082,131,9360,4092,148,9360,4091,143,9360,4153,141]"),
                position("2014-04-22 13:46:52.000", true, 22.57171, 113.86140));

        verifyPosition(decoder, buffer(
                "[SG*8800000015*0087*UD,220414,134652,A,22.571707,N,113.8613968,E,0.1,0.0,100,7,60,90,1000,50,0000,4,1,460,0,9360,4082,131,9360,4092,148,9360,4091,143,9360,4153,141]"));

        verifyPosition(decoder, buffer(
                "[SG*8800000015*0088*UD2,220414,134652,A,22.571707,N,113.8613968,E,0.1,0.0,100,7,60,90,1000,50,0000,4,1,460,0,9360,4082,131,9360,4092,148,9360,4091,143,9360,4153,141]"));

        verifyPosition(decoder, buffer(
                "[SG*8800000015*0087*AL,220414,134652,A,22.571707,N,113.8613968,E,0.1,0.0,100,7,60,90,1000,50,0001,4,1,460,0,9360,4082,131,9360,4092,148,9360,4091,143,9360,4153,141]"));

        verifyAttributes(decoder, buffer(
                "[CS*8800000015*0008*PULSE,72]"));

        verifyAttributes(decoder, buffer(
                "[3G*6005412902*0007*heart,0]"));

        verifyAttributes(decoder, buffer(
                "[3G*6005412902*0008*heart,71]"));

        verifyPosition(decoder, buffer(
                "[ZJ*014111001350304*0033*0064*UD,070318,020827,V,00.000000,N,000.000000,E,0,0,0,0,100,19,1000,50,00000000,1,255,460,0,9346,5223,42]"));

        verifyPosition(decoder, buffer(
                "[ZJ*014111001350304*0035*0097*UD,070318,020857,V,00.000000,N,000.000000,E,0,0,0,0,100,19,1000,50,00000000,5,255,460,0,9346,5223,42,9346,5214,21,9784,4083,13,9346,5222,11,9346,5221,8]"));

        verifyPosition(decoder, buffer(
                "[ZJ*014111001350304*0038*008a*UD,070318,021027,V,00.000000,N,000.000000,E,0,0,0,0,100,18,1000,50,00000000,4,255,460,0,9346,5223,42,9346,5214,20,9784,4083,11,9346,5221,5]"));
        
        verifyPosition(decoder, buffer(
                "[3G*8800000015*00DD*UD,010120,025946,V,0.0,N,0.0,E,22.0,0,-1,0,100,98,0,0,00000000,0,5,eduroam,f4:db:e6:d2:a8:00,-53,eduroam,f4:db:e6:da:d0:80,-79,eduroam,78:0c:f0:24:f9:80,-82,Lions,b0:be:76:0a:05:9a,-82,tubs-guest,f4:db:e6:d2:a8:01,-53,0.0]"));

    }

    @Test
    public void testDecodeVoiceMessage() throws Exception {

        var decoder = inject(new WatchProtocolDecoder(null));

        var mediaManager = mock(MediaManager.class);
        when(mediaManager.writeFile(any(), any(), any())).thenReturn("mock.amr");
        decoder.setMediaManager(mediaManager);

        verifyAttribute(decoder, concatenateBuffers(
                buffer("[CS*1234567890*000e*TK,#!AMR"), binary("7d5b5d2c2aff"), buffer("]")),
                Position.KEY_AUDIO, "mock.amr");

        verify(mediaManager).writeFile(any(), any(), any());

    }

}
