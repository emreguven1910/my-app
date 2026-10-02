package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Commute
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CompassAmber
import com.example.ui.theme.SunsetTerracotta
import com.example.ui.viewmodel.TravelViewModel

data class TravelRoute(
    val id: String,
    val title: String,
    val subtitle: String,
    val transport: String,
    val transportIcon: ImageVector,
    val season: String,
    val foodHighlight: String,
    val placesToSee: List<String>,
    val tips: String,
    val relatedPostId: Int
)

@Composable
fun RoutesScreen(
    viewModel: TravelViewModel,
    modifier: Modifier = Modifier
) {
    val routes = listOf(
        TravelRoute(
            id = "yht_konya",
            title = "Ankara - Konya YHT Ekspresi",
            subtitle = "Bozkırın kalbine 250 km hızla konforlu tren yolculuğu",
            transport = "Yüksek Hızlı Tren (1s 45dk)",
            transportIcon = Icons.Default.DirectionsTransit,
            season = "İlkbahar & Sonbahar",
            foodHighlight = "Konya Fırın Kebabı, Etli Ekmek, Bamya Çorbası",
            placesToSee = listOf(
                "Mevlana Müzesi & Kubbe-i Hadra",
                "Alaaddin Tepesi & Camii",
                "Şems-i Tebrizi Türbesi",
                "Tarihi Bedesten Çarşısı"
            ),
            tips = "YHT biletinizi en az 5-7 gün önceden TCDD mobil uygulamasından alın. Günübirlik seyahat için sabah 07:00 treni idealdir.",
            relatedPostId = 20
        ),
        TravelRoute(
            id = "canakkale_biga",
            title = "Çanakkale & Biga Tarih Rotası",
            subtitle = "Tarihi Yarımada şehitlikleri ve Biga'nın samimi sokakları",
            transport = "Karayolu & Boğaz Feribotu",
            transportIcon = Icons.Default.Explore,
            season = "Nisan - Ekim arası",
            foodHighlight = "Meşhur Biga Köftesi, Çanakkale Peynir Helvası",
            placesToSee = listOf(
                "Çanakkale Şehitler Abidesi",
                "57. Alay Şehitliği & Conkbayırı",
                "Kilitbahir & Çimenlik Kaleleri",
                "Biga Tarihi Çarşısı & Nilüfer Gölü"
            ),
            tips = "Şehitlikler geniş bir alana yayıldığı için rahat yürüyüş ayakkabısı ve şapka şart. Biga köftesini mutlaka yerinde deneyin.",
            relatedPostId = 22
        ),
        TravelRoute(
            id = "bursa_narli",
            title = "Narlı Sahili & Yeşil Bursa",
            subtitle = "Zeytin ağaçları eşliğinde deniz molası ve Osmanlı mirası",
            transport = "Karayolu / Deniz Otobüsü (İDO - BUDO)",
            transportIcon = Icons.Default.Explore,
            season = "Mayıs - Eylül",
            foodHighlight = "Hakiki Bursa İskender, Kestane Şekeri, Gemlik Zeytini",
            placesToSee = listOf(
                "Bursa Ulu Camii & Şadırvanı",
                "Tarihi Kozahan & Kapalıçarşı",
                "Narlı Plajı & Balıkçı Barınağı",
                "Uludağ Teleferik Seyir Terası"
            ),
            tips = "Narlı'da gün batımında sahilde yürüyüş yapın. Kozahan avlusunda közde Türk kahvesi içmeyi unutmayın.",
            relatedPostId = 5
        ),
        TravelRoute(
            id = "istanbul_tarihi",
            title = "İstanbul Tarihi Yarımada & Boğaz",
            subtitle = "Yedi tepeli şehrin kalbinde ilk keşif hatıraları",
            transport = "Şehir Hatları Vapuru & Tramvay",
            transportIcon = Icons.Default.Explore,
            season = "Dört Mevsim",
            foodHighlight = "Eminönü Balık Ekmek, Tarihi Sultanahmet Köftesi, Boza",
            placesToSee = listOf(
                "Sultanahmet Camii & Hipodrom Meydanı",
                "Ayasofya-i Kebir Cami-i Şerifi",
                "Topkapı Sarayı & Gülhane Parkı",
                "Eminönü - Kadıköy Vapur Hattı"
            ),
            tips = "İstanbulkart edinerek tramvay ve vapurları kolayca kullanın. Vapurda martılara simit atmak en güzel İstanbul klasiğidir.",
            relatedPostId = 4
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("routes_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Introduction Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Güven'in Seyahat Rotaları",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Blogda anlatılan şehirler için pratik seyahat ve lezzet rehberi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Distance & Transport Duration Calculator
        item {
            TravelCalculatorCard()
        }

        // Route Cards
        items(routes, key = { it.id }) { route ->
            RouteCard(
                route = route,
                onReadStoryClick = { viewModel.openPostDetail(route.relatedPostId) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun RouteCard(
    route: TravelRoute,
    onReadStoryClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("route_card_${route.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SunsetTerracotta.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = route.transportIcon,
                            contentDescription = null,
                            tint = SunsetTerracotta,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = route.transport,
                            style = MaterialTheme.typography.labelSmall,
                            color = SunsetTerracotta,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CompassAmber.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = route.season,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = route.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = route.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Places to See
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Görülmesi Gereken Noktalar",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                route.placesToSee.forEach { place ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = place,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Food Highlight
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = SunsetTerracotta,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Tadılacak Lezzetler: ",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = route.foodHighlight,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // Travel Tip
            Text(
                text = "💡 İpucu: ${route.tips}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button to Read Blog Story
            Button(
                onClick = onReadStoryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("read_story_button_${route.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "İlgili Seyahat Hikayesini Oku",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun TravelCalculatorCard() {
    val cities = listOf("Ankara", "Konya", "Çanakkale", "Biga", "Bursa", "Narlı", "İstanbul")
    var fromCity by remember { mutableStateOf("Ankara") }
    var toCity by remember { mutableStateOf("Konya") }

    // Distance calculation logic between blog destinations
    val pairKey = "${fromCity}_${toCity}"
    val (km, duration, transport, advice) = when (pairKey) {
        "Ankara_Konya", "Konya_Ankara" -> Quadruple(
            260,
            "1s 45dk (YHT) / 3s 15dk (Oto)",
            "Yüksek Hızlı Tren (YHT)",
            "En konforlu ve ekonomik seçenek YHT trenidir. Biletinizi önceden almanız önerilir."
        )
        "Ankara_Çanakkale", "Çanakkale_Ankara" -> Quadruple(
            660,
            "7s 30dk (Karayolu)",
            "Otobüs / Özel Araç",
            "Bursa üzerinden devam edip Çanakkale Boğazı feribotunu veya 1915 Köprüsünü kullanabilirsiniz."
        )
        "Ankara_Bursa", "Bursa_Ankara" -> Quadruple(
            385,
            "4s 15dk (Karayolu)",
            "Karayolu / Otobüs",
            "Eskişehir üzerinden keyifli bir mola vererek Bursa'ya ulaşabilirsiniz."
        )
        "Ankara_İstanbul", "İstanbul_Ankara" -> Quadruple(
            450,
            "4s 20dk (YHT) / 5s (Oto)",
            "Yüksek Hızlı Tren (YHT)",
            "YHT ile Söğütlüçeşme veya Halkalı'ya kadar konforlu ve hızlı seyahat."
        )
        "Çanakkale_Biga", "Biga_Çanakkale" -> Quadruple(
            92,
            "1s 15dk",
            "Karayolu / Minibüs",
            "Yol boyunca Marmara kıyı manzarası eşlik eder. Biga'da köfte molası mutlaka verilmeli."
        )
        "Bursa_Narlı", "Narlı_Bursa" -> Quadruple(
            55,
            "45dk - 50dk",
            "Karayolu / Sahil Yolu",
            "Gemlik Körfezi zeytinlikleri arasından geçen çok huzurlu bir sahil rotası."
        )
        "İstanbul_Çanakkale", "Çanakkale_İstanbul" -> Quadruple(
            315,
            "3s 45dk - 4s",
            "1915 Çanakkale Köprüsü / Feribot",
            "Tekirdağ üzerinden Malkara-Çanakkale Otoyolu ile hızlı ve konforlu ulaşım."
        )
        "İstanbul_Bursa", "Bursa_İstanbul" -> Quadruple(
            155,
            "1s 45dk (Deniz Otobüsü)",
            "BUDO / İDO Deniz Otobüsü",
            "Eminönü veya Kadıköy'den Mudanya'ya vapurla geçip oradan Bursa merkeze ulaşabilirsiniz."
        )
        else -> Quadruple(
            320,
            "3s 30dk",
            "Karayolu / Toplu Taşıma",
            "Mevsim koşullarına ve trafik yoğunluğuna göre hareket saatini planlayın."
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("travel_calculator_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Commute,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mesafe & Ulaşım Hesaplayıcı",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = {
                        val temp = fromCity
                        fromCity = toCity
                        toCity = temp
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Şehirleri Değiştir",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // From city chips
            Column {
                Text(
                    text = "Kalkış Noktası: $fromCity",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(cities) { city ->
                        FilterChip(
                            selected = city == fromCity,
                            onClick = { fromCity = city },
                            label = { Text(city, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // To city chips
            Column {
                Text(
                    text = "Varış Noktası: $toCity",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = SunsetTerracotta
                )
                Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(cities) { city ->
                        FilterChip(
                            selected = city == toCity,
                            onClick = { toCity = city },
                            label = { Text(city, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Result pill & stats
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Mesafe: ~$km km",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = SunsetTerracotta,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = duration,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = SunsetTerracotta
                            )
                        }
                    }

                    Text(
                        text = "Önerilen Taşıt: $transport",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "💡 $advice",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

