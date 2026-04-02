(ns lab1.lab1
  (:gen-class))

;; Функция для вычисления суммы всех элементов списка
(defn sum-list [numbers]
  (println "Начинаю вычисление суммы...")
  (let [result (apply + numbers)]
    (println "Сумма вычислена:" result)
    result))

;; Функция для вычисления произведения всех элементов списка
(defn product-list [numbers]
  (println "Начинаю вычисление произведения...")
  (let [result (apply * numbers)]
    (println "Произведение вычислено:" result)
    result))

(defn -main
  "Главная функция: запускает параллельные вычисления с future и promise"
  [& args]
  ;; Исходный список чисел (можешь изменить на свой)
  (def numbers [1 2 3 4 5])

  (println "Исходный список:" numbers)
  (println "---")

  ;; Создаём promise для синхронизации результатов
  (def sum-promise (promise))
  (def product-promise (promise))

  ;; Запускаем вычисления в параллельных потоках (future)
  (def sum-future
    (future
      (let [result (sum-list numbers)]
        (deliver sum-promise result)
        result)))

  (def product-future
    (future
      (let [result (product-list numbers)]
        (deliver product-promise result)
        result)))

  ;; Ждём завершения ОБОИХ вычислений с помощью promise
  (println "Ожидаем завершения вычислений...")

  ;; Блокируемся до тех пор, пока оба promise не будут доставлены
  (let [sum-result @sum-promise
        product-result @product-promise]

    ;; Выводим финальный результат
    (println "---")
    (println "✅ Оба вычисления завершены!")
    (println "Сумма элементов списка:" sum-result)
    (println "Произведение элементов списка:" product-result)
    (println "---")
    (println "Итоговый отчёт:")
    (println (str "  Сумма: " sum-result))
    (println (str "  Произведение: " product-result))))