;;; Small file that defines boolean algebra defined around borders in floatin-points in racket

#lang racket

(provide bt bf
         float-or float-and)


;; True and false, this is mostly bekause 0.0 and -0.0 is hard to type
(define bt 0.0)
(define bf -0.0)


(define (float-or a b)
  (+ a b))

(define (float-and a b)
  (* a b (+ a b)))
