(ns lab1.lab1
  (:gen-class))

(defn sum-list [numbers]
  (println "  Сумма: начали вычисление...")
  (let [result (apply + numbers)]
    (println "  Сумма: готово =" result)
    result))

(defn product-list [numbers]
  (println "  Произведение: начали вычисление...")
  (let [result (apply * numbers)]
    (println "  Произведение: готово =" result)
    result))

(defn sequential-compute [numbers]
  (println "Последовательное выполнение (без future):")
  (let [start (System/currentTimeMillis)
        sum (sum-list numbers)
        product (product-list numbers)
        end (System/currentTimeMillis)]
    (println "---")
    (println "Результаты:")
    (println "  Сумма =" sum)
    (println "  Произведение =" product)
    (println "Время выполнения:" (- end start) "мс")
    (- end start)))

(defn parallel-compute [numbers]
  (println "Параллельное выполнение (с future и promise):")
  (let [start (System/currentTimeMillis)
        sum-promise (promise)
        product-promise (promise)]
    (future
      (let [result (sum-list numbers)]
        (deliver sum-promise result)
        result))
    (future
      (let [result (product-list numbers)]
        (deliver product-promise result)
        result))
    (let [sum @sum-promise
          product @product-promise
          end (System/currentTimeMillis)]
      (println "---")
      (println "Результаты:")
      (println "  Сумма =" sum)
      (println "  Произведение =" product)
      (println "Время выполнения:" (- end start) "мс")
      (- end start))))

(defn -main [& args]
  (let [numbers [1 2 3 4 5]]
    (println "Список чисел:" numbers)
    (println "========================")

    (def sequential-time (sequential-compute numbers))
    (println "========================")

    (def parallel-time (parallel-compute numbers))
    (println "========================")

    (println "СРАВНЕНИЕ:")
    (println "  Последовательное время:" sequential-time "мс")
    (println "  Параллельное время:" parallel-time "мс")
    (println "  Выигрыш:" (- sequential-time parallel-time) "мс")

    (shutdown-agents)))