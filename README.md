# wordForget
a word coach without word list.

## format
words must match word+"**\\t**"+meaning, and each line can contain only one word.

you may need to change the format if your vocabulary list was copied from somw website. (tips: try excel?)

if your computer can't input tab, copy one from somewhere else.  

## args
`path=<words list file> savePath=<the data about how much you Forgot should be placed>`

save path is not necessary.

## usage 
`q` for exit program

`start` for start

input `0` means you don't know this word, `1` for you know it;

input `k` for skip this word(you will never see it anymore), `u` for undo last "k" or "u" command;

input nothing(just press Enter key) to display meaning;

`w` for save, `q` for save & exit.

so what about `wq`? sorry, it's not vim ;)
