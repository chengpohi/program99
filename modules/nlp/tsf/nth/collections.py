import tensorflow as tf
from tensorflow.python.data.ops.dataset_ops import TensorSliceDataset

sess = tf.InteractiveSession()

print(tf.reshape(list(range(0, 4)), [-1, 2, 2, 1]).eval())
print("-----" * 5)
print(tf.reshape(list(range(0, 9)), [3, 3]).eval())

print("-----" * 5)
r = tf.reshape(list(range(0, 9)), [3, 3]).eval()

print("-----" * 5)
print(tf.reshape(r, [-1]).eval())

print("-----" * 5)
print(tf.reshape(r, [3, 3]).eval())

print("-----" * 5)
print(tf.reshape(r, [9]).eval())

print("-----" * 5)
print(tf.reshape(list(range(0, 18)), [2, 3, 3]).eval())
print("-----" * 5)
print(tf.reshape(list(range(0, 18)), [2, 3, 3, 1]).eval())

print("-----" * 5)
print(tf.reshape(list(range(0, 18)), [1, 3, 3, 2]).eval())

print("-----" * 5)
print(tf.reshape(list(range(0, 18)), [2, 1, 3, 3]).eval())

print("-----" * 5)
print(tf.reshape(list(range(0, 18)), [2, 3, 1, 3]).eval())

dataset1: TensorSliceDataset = tf.data.Dataset.from_tensor_slices(tf.random_uniform([4, 10]))
print(dataset1.output_types)
print(dataset1.output_shapes)

dataset2: TensorSliceDataset = tf.data.Dataset.from_tensor_slices(
    (tf.random_uniform([4]),
     tf.random_uniform([4, 100], maxval=100, dtype=tf.int32)))

print(dataset2.output_types)
print(dataset2.output_shapes)

dataset = tf.data.Dataset.range(100)
iterator = dataset.make_one_shot_iterator()
next_element = iterator.get_next()

for i in range(100):
    value = sess.run(next_element)
    assert i == value

x = tf.placeholder(tf.float32)
y = tf.placeholder(tf.float32)

z = x + y

print(sess.run(z, feed_dict={x: 1, y: 2}))
print(sess.run(z, feed_dict={x: [1, 3], y: [2, 4]}))

my_data = [
    [0, 1, ],
    [2, 3, ],
    [4, 5, ],
    [6, 7, ],
]

slices = tf.data.Dataset.from_tensor_slices(my_data)
next_item = slices.make_one_shot_iterator().get_next()

while True:
    try:
        print(sess.run(next_item))
    except tf.errors.OutOfRangeError:
        break

x = tf.placeholder(tf.float32, shape=[None, 3])
y = tf.layers.dense(x, units=1)

init = tf.global_variables_initializer()
sess.run(init)

print(sess.run(y, {x: [[1, 2, 3], [4, 5, 6]]}))

constant = tf.constant([1, 2, 3])
tensor = constant * constant

print(tensor.eval())

a = tf.constant(2)
b = tf.constant(5)
result = tf.cond(a < b, lambda: tf.add(a, b), lambda: tf.square(b))
print(result.eval())

data = tf.data.Dataset.range(18)
x = tf.placeholder(tf.float32, shape=[None, 9, 2])

# FLATTEN
print(tf.contrib.layers.flatten(x).get_shape().as_list())

rr = tf.reshape(list(range(0, 9)), [3, 3])

print(rr.eval())
print(tf.expand_dims(rr, 0).eval())

print(tf.reshape(rr, [1, 3, 3]).eval())

res = tf.train.batch([rr], 1)

print(rr.get_shape())

coord = tf.train.Coordinator()
threads = tf.train.start_queue_runners(coord=coord)
print(res.eval())
coord.request_stop()
coord.join(threads)

d1 = tf.data.TextLineDataset("vocab.py")
d2 = tf.data.TextLineDataset("vocab.py")

res = tf.data.Dataset.zip((d1, d2))
src_tgt_dataset = res.map(
    lambda src, tgt: (tf.string_split([src]).values, tf.string_split([tgt]).values)
)
constant = tf.constant("hello world")
print(tf.string_split(["hello world"]).values.eval())
tv = tf.Variable("hello world")
