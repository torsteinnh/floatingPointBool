;;; Small file that defines boolean algebra defined around borders in floating-points in racket

#lang racket

(provide bt bf
         ; Many functions should be added here sometime
         )



;; True and false, this is mostly bekause 0.0 and -0.0 is hard to type

(define bt 0.0)
(define bf -0.0)



;; Primitives

(define (f-or a b)
  (+ a b))

(define (f-not a)
  (- a))

(define (f-xnor a b)
  (* a b))



;; Simple komposittes shortened

(define (f-and a b)
  (* a b (+ a b)))

(define (f-nand a b)
  (- (* a b (+ a b))))
 
(define (f-xor a b)
  (- (* a b)))

(define (f-a=>b a b)
  (- b a))

(define f-a<=>b f-xnor)



;; More advanced funny stuff

(define (f-fadder a b c)
  ; Takes inn a, b and carry inn, returns out and carry out in a cons
  (let ((out (* a b c))
        (carry (* (+ a b) (+ c (* a b)))))
    (cons out carry))
  )

