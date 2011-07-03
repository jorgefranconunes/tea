;;; tea-mode.el --- a major-mode for editing Tea scripts.
;;
;; Copyright (C) 2001 PDM&FC
;;
;; Author: Jorge Nunes (jorge.nunes@pdmfc.pt)
;;





(defgroup tea nil
  "Major mode for editing Tea code."
  :prefix "tea-"
  :group 'languages)

(defcustom tea-mode-hook nil
  "Hooks called when Tea mode fires up."
  :type 'hook
  :group 'tea)

(defcustom tea-indent-level 4
  "Amount by which Tea statements are indented."
  :type 'integer
  :group 'tea)

(defvar tea-mode-map nil
  "Keymap used with Tea mode.")

(defvar tea-font-lock-keywords
  (eval-when-compile
    (list
     '("\\<\\(define\\|foreach\\|global\\|set!\\)\\>[ \t]*\\(\\sw+\\)?"
       (1 font-lock-keyword-face) (2 font-lock-variable-name-face nil t))
     '("\\<\\(class\\)\\>[ \t]*\\(\\sw+\\)?[ \t]*\\(\\sw+\\)?"
       (1 font-lock-keyword-face) (2 font-lock-type-face nil t) (3 font-lock-type-face nil t))
     '("\\<\\(method\\)\\>[ \t]*\\(\\sw+\\)?[ \t]*\\(\\sw+\\)?"
       (1 font-lock-keyword-face) (2 font-lock-type-face nil t) (3 font-lock-function-name-face nil t))
     '("\\<\\(break\\|continue\\|echo\\|exit\\|if\\|import\\|is\\|while\\|return\\)\\>"
       (1 font-lock-keyword-face))
     ))
  "Default expressions to highlight in Tea modes.")





(defvar tea-mode-syntax-table
  (make-syntax-table)
  "The syntax table.")

(let ((i 0))
  (while (< i ?0)
    (modify-syntax-entry i "w   " tea-mode-syntax-table)
    (setq i (1+ i)))
  (setq i (1+ ?9))
  (while (< i ?A)
    (modify-syntax-entry i "w   " tea-mode-syntax-table)
    (setq i (1+ i)))
  (setq i (1+ ?Z))
  (while (< i ?a)
    (modify-syntax-entry i "w   " tea-mode-syntax-table)
    (setq i (1+ i)))
  (setq i (1+ ?z))
  (while (< i 128)
    (modify-syntax-entry i "w   " tea-mode-syntax-table)
    (setq i (1+ i)))
  (modify-syntax-entry ?  "    " tea-mode-syntax-table)
  (modify-syntax-entry ?\t "    " tea-mode-syntax-table)
  (modify-syntax-entry ?\f "    " tea-mode-syntax-table)
  (modify-syntax-entry ?\n ">   " tea-mode-syntax-table)
  (modify-syntax-entry ?\^m ">   " tea-mode-syntax-table)
  (modify-syntax-entry ?' "\"   " tea-mode-syntax-table)
  (modify-syntax-entry ?. "_   " tea-mode-syntax-table)
  (modify-syntax-entry ?# "<   " tea-mode-syntax-table)
  (modify-syntax-entry ?\" "\"    " tea-mode-syntax-table)
  (modify-syntax-entry ?\\ "\\   " tea-mode-syntax-table)
  (modify-syntax-entry ?\( "()  " tea-mode-syntax-table)
  (modify-syntax-entry ?\) ")(  " tea-mode-syntax-table)
  (modify-syntax-entry ?\{ "(}  " tea-mode-syntax-table)
  (modify-syntax-entry ?\} "){  " tea-mode-syntax-table)
  (modify-syntax-entry ?\[ "(]  " tea-mode-syntax-table)
  (modify-syntax-entry ?\] ")[  " tea-mode-syntax-table))

 





;;;###autoload
(defun tea-mode ()
  "Major mode for editing Tea scripts."
  (interactive)
  (let ((switches nil)
	s)
    (kill-all-local-variables)
    (setq major-mode 'tea-mode)
    (setq mode-name "Tea")
    (set (make-local-variable 'indent-line-function) 'tea-indent-line)
    (set (make-local-variable 'comment-start) "# ")
    (set (make-local-variable 'font-lock-defaults)
	 '(tea-font-lock-keywords nil nil ((?_ . "w") (?: . "w"))))
;    (or tea-mode-map
;	(tea-setup-keymap))
    (tea-setup-keymap)
    (use-local-map tea-mode-map)
;    (set-syntax-table (copy-syntax-table))
;    (modify-syntax-entry ?# "<")
;    (modify-syntax-entry ?\n ">")
    (set-syntax-table tea-mode-syntax-table)
    (setq indent-tabs-mode nil)
    (run-hooks 'tea-mode-hook)))
  





(defun tea-setup-keymap ()
  "Set up keymap for Tea mode."
  (setq tea-mode-map (make-sparse-keymap))
  ;; indentation
  (define-key tea-mode-map [?}] 'tea-electric-brace)
  (define-key tea-mode-map [?]] 'tea-electric-bracket)
  (define-key tea-mode-map [?\)] 'tea-electric-par)
  (define-key tea-mode-map [?\n] 'tea-electric-eol)
  (define-key tea-mode-map [?\r] 'tea-electric-eol))





(defun tea-indent-line ()
  "Indent current line as Tea code.
Return the amount the indentation changed by."
  (let ((indent (tea-calculate-indentation nil))
	beg shift-amt
	(case-fold-search nil)
	(pos (- (point-max) (point))))
    (beginning-of-line)
    (setq beg (point))
    (skip-chars-forward " \t")
    (save-excursion
      (while (let ((c (following-char)))
	       (or (eq c ?}) (eq c ?\])))
	(setq indent (max (- indent tea-indent-level) 0))
	(forward-char 1)
	(if (looking-at "\\([ \t]*\\)}")
	    (progn
	      (delete-region (match-beginning 1) (match-end 1))
	      (insert-char ?  (1- tea-indent-level))))))
    (setq shift-amt (- indent (current-column)))
    (if (zerop shift-amt)
	(if (> (- (point-max) pos) (point))
	    (goto-char (- (point-max) pos)))
      (delete-region beg (point))
      (indent-to indent)
      ;; If initial point was within line's indentation,
      ;; position after the indentation.  Else stay at same point in text.
      (if (> (- (point-max) pos) (point))
	  (goto-char (- (point-max) pos))))
    shift-amt))





(defun tea-calculate-indentation (&optional parse-start)
  "Return appropriate indentation for current line as Tea code.
In usual case returns an integer: the column to indent to."
  (let ((pos (point)))
    (save-excursion
      (if parse-start
	  (setq pos (goto-char parse-start)))
      (beginning-of-line)
      (if (bobp)
	  (current-indentation)
	(forward-char -1)
	(if (eq (preceding-char) ?\\)
	    (+ (current-indentation)
	       (progn
		 (beginning-of-line)
		 (if (bobp)
		     (* 2 tea-indent-level)
		   (forward-char -1)
		   (if (not (eq (preceding-char) ?\\))
		       (* 2 tea-indent-level)
		     0))))
	  (forward-char 1)
	  (if (re-search-backward
	       "\\(^[^ \t\n\r#]\\)\\|\\({\\s *[#\n]\\)\\|\\(\\[\\s *[#\n]\\)\\|\\(}\\s *\n\\)\\|\\(\\]\\s *\n\\)"
	       nil  t)
	      (+ (- (current-indentation)
		    (if (save-excursion
			  (beginning-of-line)
			  (and (not (bobp))
			       (progn
				 (forward-char -1)
				 (eq (preceding-char) ?\\))))
			(* 2 tea-indent-level)
		      0))
		 (if (let ((c (following-char)))
		       (or (eq c ?{) (eq c ?\[)))
		     tea-indent-level
		   0))
	    (goto-char pos)
	    (beginning-of-line)
	    (forward-line -1)
	    (current-indentation)))))))





(defun tea-electric-brace (arg)
  "Insert `}' and indent line for Tea."
  (interactive "P")
  (tea-electric-end ?} arg))

(defun tea-electric-bracket (arg)
  "Insert `]' and indent line for Tea."
  (interactive "P")
  (tea-electric-end ?] arg))

(defun tea-electric-par (arg)
  "Insert `)' and indent line for Tea."
  (interactive "P")
  (tea-electric-end ?\) arg))

(defun tea-electric-eol (arg)
  "Insert `\n' and indent line for Tea."
  (interactive "P")
  (insert-char ?\n (prefix-numeric-value arg))
  (tea-indent-line))
  





(defun tea-electric-end (c arg)
  "Insert the argument character and indent line for Tea."
  (insert-char c (prefix-numeric-value arg))
  (tea-indent-line)
  (blink-matching-open))





(provide 'tea-mode)



