Список продуктов для чтения<br><br>
<code>
private val listSubs = listOf(ProductPreview("id", "title", "description")
</code><br><br>
инициализация

<code>
try {
            billing = Billing(this, this, { /*service coneected error body*/ }, listSubs)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
</code><br><br>

открытие модалки покупки

<code>

try {
            billing?.products?.get(0)?.let {
                billing?.showFormPurchaseProduct(it)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

</code><br><br>

проверка есть ли хоть одна подписка

<code>

        try {
            billing?.isSubs { isSubs ->
                
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    
</code>