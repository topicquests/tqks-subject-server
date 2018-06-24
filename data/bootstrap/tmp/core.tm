# important stuff
#TODO: model varieties of is/ar
#consider another field: truth, e.g. true, false
#consider another field: tense, e.g. past, present, future
#move all the verbs up here
# The first term in each sequence is the canonical form
#Structure: id :: type :: <data> :: tense :: negation :: inverse (or empty) :: LensCode (NEW!!!)
# LensCode is found only in canonical form
80000 :: prep :: in
80001 :: prep :: since
80002 :: prep :: before
80003 :: prep :: during
80004 :: prep :: long before
80005 :: prep :: shortly before
80006 :: prep :: long after
80007 :: prep :: shortly after

90000 :: conj :: and
90001 :: conj :: ,

95000 :: disj :: or

#Controlled English predicates
123290 :: verb :: is a :: present :: + ::  :: TaxonomicLens
123290 :: propertytype :: subOf
123291 :: verb :: is a particular :: present :: + ::  :: TaxonomicLens
123291 :: propertytype :: instanceOf

123300 :: verb :: is :: present :: +
123300 :: verb :: are :: present :: +
123300 :: verb :: was :: past :: +
123300 :: verb :: used to be :: past :: +
123300 :: verb :: will be :: future :: +

123350 :: verb :: change :: present :: + ::  :: ChangeLens
123350 :: verb :: changed :: past :: +
123350 :: verb :: has changed :: past :: +
123350 :: verb :: has significantly changed :: past :: +
123350 :: verb :: has barely changed :: past :: +
123350 :: verb :: is changed by :: present :: + :: inverse
123350 :: verb :: was changed by :: past :: + :: inverse
123350 :: verb :: will be changed by :: future :: + :: inverse

##make a node for the verbs that follow
123400 :: assertiontype :: CausalRelation :: Causal Relation :: A causes B

123400 :: verb :: cause :: present :: + ::   :: CauseLens
123400 :: verb :: causes :: present :: +
123400 :: verb :: will cause :: future :: +
123400 :: verb :: caused :: past :: +
123400 :: verb :: has caused :: past :: +
123400 :: verb :: is a cause of :: present :: +
123400 :: verb :: is the cause of :: present :: +
123400 :: verb :: is a primary cause of :: present :: +
123400 :: verb :: is the primary cause of :: present :: +
123400 :: verb :: are the primary causes of :: present :: +
123400 :: verb :: is a causal factor in :: present :: +
123400 :: verb :: is causing :: present :: +
123400 :: verb :: are causing :: present :: +
123400 :: verb :: can cause :: present :: +
123400 :: verb :: dominant cause :: present :: +

123400 :: verb :: is caused by :: present :: + :: inverse
123400 :: verb :: was caused by :: past :: + :: inverse
123400 :: verb :: will be caused by :: future :: + :: inverse
## close out the node
123400 :: finish

123401 :: assertiontype :: NotCausalRelation :: Not Causal Relation :: A does not cause B

123401:: verb :: not cause :: present :: - ::  :: CauseLens
123401:: verb :: cannot cause :: present :: -
123401:: verb :: can not cause :: present :: -
123401:: verb :: does not cause :: present :: -
123401 :: verb :: is not caused by :: present :: - :: inverse
123401 :: verb :: will not be caused by :: future :: - :: inverse
123401 :: verb :: can not be caused by :: present :: - :: inverse
123401 :: verb :: cannot be caused by :: present :: - :: inverse
123401 :: verb :: was not caused by :: past :: - :: inverse
123401 :: verb :: will not cause :: future :: -
123401 :: verb :: has not caused :: past :: -
123401 :: verb :: did not cause :: past :: -
123400 :: finish


123400 :: contradiction :: 123401































